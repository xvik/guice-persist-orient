# Repository internals

Repository methods using guice aop and so they could be used in any place.
But it's more natural to use them on interface methods.

Support for using interfaces and abstract classes in guice is provided by [guice-ext-annotations](https://github.com/xvik/guice-ext-annotations) library. 
Runtime proxies could be also used (and where used initially) but this way guice aop couldn't be used, which is limitation. Class generation overcomes this limitation making abstract types valid guice beans with aop support.

Repositories are build using plugin architecture. Each repository annotation is a plugin and can be replaced if requied.

## How repositories work

Repository method executor `RepositoryMethodInterceptor` is applied using guice aop: to all methods with annotations, annotated with `@RepositoryMethod`.
It is not allowed to use multiple repository method annotations on the same method. If any repository contains such method guice context startup will fail (checked explicitly).

During context startup descriptors not computed! This would slow down startup significantly. 
Descriptor is always computed on first method call. 
So if context start successfully it doesn't mean all repository methods are valid.

On repository method call, `MethodDescriptorFactory` either return cached method descriptor or:
 
* Calls method extension to create descriptor 
* Extension creates descripotor and initialize required fields (possibly set hints in descriptor)
* Extension calls `SpiService.process` with `ParamsContext` instance in order to resolve all extensions
  - Spi service calls `ParamsService` to process all parameters and param extensions
  - At the end of params processing params service calls `ParamsContext.process` which analyze all parameters info (and additional information provided by extensions) and creates method specific parameters context and assigns it to method context
  - Next `AmendExtensionsService` called to resolve all compatible amend extensions.
  - Parameters extensions (holder in ParamsContext) are also checked is they are amend extensions
  - Sorted list of compatible amend extensions (including global) is assigned to descriptor object
  - At the end `ResultService` called to find converter extensions 
  - Result service compose `ResultConversionDescriptor` and assigns it to method descriptor
* After descriptor is created by extension
  - Method return type analyzed to compose result descriptor (descriptor.result filed). Affected by returnCollectionHint hint.
  - Using result analysis, executor instance selected and assigned to descriptor (descriptor.executor). Affected by connectionHint.
* Method extension assigned to descriptor object (as Provider).

Execution:

* Extension obtained from descriptor and called to perform execution
* Extension executes all amend extension from descriptor
* Extension use executor from descriptor if required (e.g. command extension use it to execute query and delegate use it to obtain connection instance in @Connection extension)
* Extension returns raw execution result

Result conversion:

* Default result converter is called (if not disabled by result extension) to convert result
* Call result conversion extension if defined
* Finally result returned

## Repository extensions

Repositories support 4 types of extensions:

* Method extension - defines repository method logic
* Repository parameter extension - defines special handling logic for method parameters
* Amend method extension - extension that influence method behaviour (many parameter extensions are amend extensions also)
* Result converter extension - extension to extend or modify default result conversion logic 

### Method extension

Repository method execution consists of three phases: method descriptor creation, execution and result conversion. Descriptor is created on first call and cached. This is required, because this way first call will be slower, but will be able to perform any heavy computations to validate definition and extensions processing and prepare everything for fast execution on future calls.

##### Method descriptor

Repository descriptor object must extend `RepositoryMethodDescriptor`. Each method could have unique descriptor or some methods could share the same descriptor type.
For example, `@Query`, `@Function`, `@AsyncQuery` use the same descriptor type `SqlCommandDescriptor` and @Script use uxtended `ScriptCommandMethodDescriptor`.
`@Delegate` use completely different descriptor object `DelegateMethodDescriptor`. 

There are two hint fields in `RepositoryMethodDescriptor`: returnCollectionHint and connectionHint.
Extension may set them to affect return collection type (handled by result converter) and guide executor selection.

Descriptor object will be passed to all extension, so it defines which data will be visible to all method specific extensions. Internally root descriptor has `extDescriptors` map to let extensions store any extension specific data.

##### Repository method extension

Repository method is defined by annotation, annotated with `@RepositoryMethod`. Annotation contains actual extension class, which implements `RepositoryMethodExtension`.

Extension is resolved from guice context. Prefer using singletons.
Note that extension instance is registered as Provider, so if, for example, prototype scope used, new extension instance will be used for each execution (as a drawback different extension instances will be used for descripotr creation and first execution).

Generics are **very important**! 
`RepositoryMethodExtension` must declare used descriptor object in generic (T). Later this will be used to filter other extensions.
Descriptor object also must be generified with supporting amend extensions type (see amend extesnions below). Only extension knows how it would be executed and so only it could define extension interface and properly use it. 
For example, command and delegate methods are completely different and so use different extensions interface.

Method annotation is searched on methods and on type. You can limit exact extension scope when defining  annotation target.
For example, command extensions applies only to method and delegate could be defined on type.

Extension must call `SpiService.process` method to resolve all amend extension, process parameters and extensions and resolve result converter extensions.

##### Parameters context

Method extension defines param context object extending `ParamsContext`. Params context is used by parameter extensions. It's methods define core parameter types.
For example, command extensions support positional and named query arguments and also el varables (in query string). Delegate support only target method parameters.

Also, params context in responsible for main descriptor update after all parameter extensions processed: it validates all parameters info and updates descriptor for fast processing on execution. Read more about it in parameters extensions section below. 

### Parameter extension

Parameter extensions resolved from parameter annotations.
Parameter extension annotation must be annotated with `@MethodParam`. Annotation defines extension class, which must implement `MethodParamExtension`.
Extension instance is obtained from guice context. Prefer using singleton scope.

Again generics are important! During resolution parameter extension checked for compatibility using descriptor type generic. If type defined in extension genric is assignable to current descriptor type - extension is compatible. If some method extensions use descriptors hierarchy, extension may define some middle type in generic to be compatible with all methods.
For example, `@Script` extension use special descriptor which extends `CommandMethodDescriptor` and all parameter extensions which use `CommandMethodDescriptor` in generic are compatible with `@Script` (because declared descriptor type is compatible).

All parameters are parsed before calling extension. Extension receive all parameters under extension annotation.
This is very handy for validation: for example, some extension requires only one annotated parameter (`@Size`, `@Limit` etc).

Param extension may use main descriptor's extDescriptors map to store extension specific data, which will be used during execution.

Param extension itself is called only on parameters parsing. If extension need to perform some execution time modifications, it must also implement amend extension interface, specific for repository method.
In this case parameter extension is registered as amend extension.

If parameter extension in universal and could be applied to multiple method types it must not only declare compatible descriptor in generic, but also implement all extension interfaces (if target methods have different extension interfaces). 
If parameter extension doesn't implement compatible amend extension interface it simply will not be registered in method amend extensions (the same as if param extension did not implement any amend interface at all).

Parameter extension is checked for amend extension compatibility using amend extension type defined in main descriptor generic (RepositoryMethodDescriptor, generic E).

Implementation guideline:

* Define public static String filed KEY as extension class full name.
* Define extension specific descriptor object. 
* During parameters processing, validate (if possible), fill specific descriptor and save it in main descriptor:
descriptor.extDescriptors.put(KEY, specificDescriptor).
* For validation use MethodDefinitionException.chec() static method to check and throw exception.
* In amend extension method you can obtain descriptor descriptor.extDescriptors.get(KEY) and use it.
* During execution (in amend extension methods) use MethodExecutionException.checkExec static method to check and throw exception.

Only one parameter extension annotation could be defined on single method parameter.

Parameters context contains descriptor context object, which may be used to get parsed generics info and root repository class in extension (note that generics object is set to method declaring type by default).

### Amend extensions

Amend extensions are execution time extensions. They influence method execution by modifying data objects, configuring something etc.

Each repository method extension defines its own extension. Such extension interface must extend `AmendExecutionExtension`.

Often amend extensions are registered from parameters extensions. But in some cases, amend extension should be driven by separate annotation. Fro example, @Timeout aextension.

Amend extension annotation must be annotated with `@AmendMethod`, which contains actual extension class.
Extension instance is obtained from guice context. Prefer using singleton scope.

Amend annotation extension must implement AmendMethodExtension. 
Generics are important, because descriptor type is used to check extension compatibility.

Amend extension annotations are searched on method, on type and on root repository type.
If annotation defined both on method and type, more specific method annotation used and type annotation ignored (the same if annotation defined on repository type and method declaring type - declaring type is more specific).

If annotation defined directly on method and it's not compatible (by descriptor) exception will be thrown indicating bad usage. 
Incompatible amend annotations found on type or root type are simply ignored. The logic is simple: you may have many methods in repository which must be annotated and only one which is incompatible. You can simpy annotate type to apply annotation for all methods and incompatible method will ignore this annotation.

Amend annotation extension may not implement method specific extension interface, in this case it will be executed on repository creation only. In order to affect execution, repository method specific extension interface must be implemented (same as with parameters).

If amend extension implements incompatible method specific amend extension interface it will be ignored.
So if amend extension is compatible, but execution extension interface is not - extension is executed during descriptor creation, but not registered as execution amend extension.

As with parameter extensions, use extension specific descriptor and save it in main method descriptor.
Use MethodExecutionException and MethodExecutionException static methods for validation in annotation processing and execution methods.

Amend extension are sorted using `@Order` annotation. 

All resolved compatible amend extensions are stored in main method descriptor and must be used by method extensions to process extensions (it is impossible to automate, because extensions are method specific and only method knows how and when to process them).

#### Global amend extension

It is possible to register global amend extensions, which will apply to all executed methods (if compatible).
Use `AmendExtensionsService.addGlobalExtension(ext)`.
You can also remove global extension later using `removeGlobalExtension` method.

But remember, that compatible amend extensions are stored in the descriptor, which is computed one time and used for all method executions. So if you register global extension after descriptor creation, it will not affect that method. The same with remove: descriptors created while global extension was registered will still use it. 

Of course, you can always clear descriptors cache using `MethodDescriptorFactory.clearCache()`. It will destroy all already pre-computed descriptor and on next call new descriptors will be created.

Better approach is to register global extension together with application start (when no repository methods were called).

### Result converter extensions

By default, after every repository method execution result is converted with `ResultConverter`. 
Default implementation do simple conversions between collection types, convertion from list to single element etc.
Default implementation may be changed in guice module.

If you want to extend or replace default converter behaviour for single method you can use result converter extension.

Extension declared using annotation, annotated with `@ResultConverter`, which contains actual extension class.
Extension instance is resolved from guice context. Prefer using singleton scope.
Converter us used as Provider so in case of, for example, prototype scope, new instance will be used for each conversion.

Pay attention to `applyDefaultConverter` annotation parameter. It declares if default converter should be applied.
This allows you either extend default behaviour or completely replace it.
Note that default converter is executed before extension.

Extension is searched on method and method declared type. Only one result converter extension is allowed,
but if annotation declared on type and method - method annotation will be used.

Extension must implement `ResultExtension` interface. As with other extensions, main descriptor could be used
to store extension specific data (e.g. some annotation parameters).

You may use `RepositoryMethodDescriptor.result` descriptor in converter logic. Result descriptor contains method result analysis info (it is used by default converter).

## Repository executor

`RepositoryExecutor` abstracts connection specific calls from repository method processing.
Executor is selected by method return type:

* If result is `ODocument` (or generic) then document connection used
* If result is model class then object connection used
* If result id `Vertex` or `Edge` then graph connection used

Connection object defines type of result types (executing the same query with different connections could return different result objects).

User may influence executor selection mechanism by setting required connection type in method annotation.
Also, extensions may use descriptor hint to affect executor selection (executor selection performed after descriptor creation and all extensions processing).

Default executor implementations may be overridden in `RepositoryModule`.

```java
public class MyRepositoryModule extends RepositoryModule {
    @Override
    protected void configureExecutors() {
          bindExecutor(CustomDocumentFinderExecutor.class);
          bindExecutor(CustomObjectFinderExecutor.class);
          bindExecutor(CustomGraphFinderExecutor.class);
    }      
}
```

As with connections support, executors are registered according to classpath.