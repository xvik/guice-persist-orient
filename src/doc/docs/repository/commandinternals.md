# Command methods implementation details

Command method extensions applied to all command extensions (`@Query`, `@Function`, `@Script` etc.)

!!! tip
    Read [repository internals](internals.md) first for better understanding

## Common command features

Commands generally support:

* Positional parameters
* Named parameters
* Command string el variables

Most commands use `CommandMethodDescriptor` as descriptor, except `@Script` which use `ScriptCommandMethodDescriptor` (to store script language). So when you target `CommandMethodDescriptor` (in generic), extension will be compatible with all command methods.

All command extensions use `AbstractCommandExtension` as base class. It's very easy to add your own custom command annotation using it (look bundled extensions sources).

Command extensions have three execution phases:

* `SqlCommandDescriptor` composition
* `OCommandRequest` composition (orient command)
* And command execution 

Amend extension shoudl implement `CommandExtension` insterface (to affect execution).

### SqlCommandDescriptor phase

`SqlCommandDescriptor` contains query, prepared parameters (composed from method arguments) and el variables (also composed fro arguments). Extensions could modify query (for example, @Skip appends "SKIP" part to query string. Extensions could add (or modify) query parameters. And extensions could add/modify el variables.

Positional parameters is a core feature (its parameters without any extension), anyway they could be modified too (for example, `@DynamicParams` modifies them).

Named parameters covered with bundled extension `@Param`. It is not mandatory to use it: you can write your own extension to define named parameters.

El variables defined with `@ElVar` extension, but again, its not mandatory - you can use your own extension to define variables.

### OCommandRequest phase

First of all, el variables replaced in query, producing target query string.
Then command object created using prepared query.

Extensions could modify resulted command object (for example, `@Timaout`, `@Listen`).

After all command object wrapped with connection object (using selected RepositoryExecutor) and executed, using composed parameters.
Command wrapping is required to bind command to specific connection type. This will affect resulting objects.

### Extensions lifecycle

* Create `SqlCommandDescriptor`
* Run extensions to amend descriptor
* Apply el vars and create `OCommandRequest`
* Run extensions to amend request object
* Wrap command with connection and execute with prepared parameters

### Command parameters context

Command extensions use `CommandParamsContext` during parameters processing.

Parameter extensions are called after all parameters processing (to collect all parameters, marked with the same annotation)

If you need to modify positional parameters, you can obtain them through `paramsContext.getOrdinals()`. Usually, there is no need to modify them, because if you need special parameter handling you'll define custom annotation.

If your extension defines named parameter you may register it in context: `paramsContext.addNamedParam()`.
After registration, it will be applied automatically. 
You may omit registration if your parameter must be converted or something like this (in this case you'll use amend extension to manually apply parameter value).

If your extension handles el variable you must declare it either as static `paramsContext.addStaticElVarValue()` if value is the same for all method executions, or declare it as dynamic `paramsContext.addDynamicElVarValue()`.
In case of dynamic el var, you'll need to use amend extension to manually apply value during execution.

Registration of el variables is required, because they are strictly checked and if some not declared variable found in query or declared variable not used in query error will be thrown to indicate method definition error. 

If you need access to method, type, know root repository type or use generics info, use `paramsContext.getDescriptorContext()`. 
Note that if parameter type is generified, you will receive resolved generic in extension.

## Implementing command parameter extension

Suppose you want to implement `@MyCustomParam` parameter extension.

Declaring annotation:

```java
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(MyCustomParamExtension.class)
public @interface MyCustomParam { ... }
```

If required, define extension options as annotation attributes. `@MethodParam` marks extension as parameter extension and it will be found and `MyCustomParamExtension` class obtained from guice context and executed.

Now implement extension:

```java
@Singleton
public class MyCustomParamExtension implements 
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, MyCustomParam >{ 
    
    @Override
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<MyCustomParam>> paramsInfo) {
       ...
    }
}
```

This extension will be called once, during descriptor creation. Here you can validate parameters and, for example, register named parameter or el variable in paramsContext 

!!! note
    We declare `MethodParamExtension<CommandMethodDescriptor ...>`, this means extension is compatible only with command descriptor and can't be used with delegate (or some other) method extensions. Also, because `@Script`'s `ScriptCommandMethodDescriptor` extends CommandMethodDescriptor, this extension is compatible with scripts too.

If you need some execution time modification, you'll need to implement amend extension interface:

```java
@Singleton
public class MyCustomParamExtension implements 
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, MyCustomParam>,
        CommandExtension<CommandMethodDescriptor> { 

    public static final String KEY = MyCustomParamExtension .class.getName();

    ...
    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        ...
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        ...
    }
```

By implementing `CommandExtension` we did our extension compatible with command methods amend extension. Now parameter extension will be registered as amend extension afeter parameters processing.

Most likely you'll need some information from parameters processing method inside executino methods.
If it's not a simple value (int, string etc), define your value object `MyCustomParamDescriptor`.
Compose descriptor on processing phase and store inside main method descriptor:

```java
descriptor.extDescriptors.put(KEY, yourCustomDescriptor);
```

Now in execution method you can obtain it and use:

```java
MyCustomParamDescriptor desc = (MyCustomParamDescriptor) descriptor.extDescriptors.get(KEY);
```

!!! note 
    KEY is public. This will allow you to use it in unit tests and possibly, other extensions also could use it (for example, `@AsyncQuery` must know about `@Listen` extension).

That's all, extension could be used in command method:

```java
@Query("select from Model")
List select(@MyCustomParam String param);
```

Look bundled extensions sources for advanced examples.

## Implementing command method amend extension

First of all, you can register custom amend extension globally:

```java
@Inject AmendExtensionsService amendExtensions;
...
amendExtensions.addGlobalExtension(new CommandExtension() {...});
```

Or you can use custom annotation, for example:

```java
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@AmendMethod(MyCustomAmendExtension.class)
public @interface MyCustomAmend{ ... }
```

Amend extensions are searched on method, on type and on root repository type. 
You can limit usage scope with `@Target` annotation (e.g. allow usage only on methods).
`@AmendMethod` marks annotation as amend annotation, now it will be resolved.

As with parameters, you eaither implement just parsing interface, to apply extension in descriptor creation time, or implement also amend extension interface, to use extension during method call.

```java
@Singleton
public class MyCustomAmendExtension implements 
        AmendMethodExtension<CommandMethodDescriptor, MyCustomAmend>,
        CommandExtension<CommandMethodDescriptor> {

    @Override
    public void handleAnnotation(final CommandMethodDescriptor descriptor, final MyCustomAmend annotation) {
        ...
    }
    
    ...
}
```

Implementation guideline is the same as with params: use public KEY field and custom descriptor object.

Extension could be used like this:

```java
@Query("select from Model")
@MyCustomAmend
List select();
```

See [@Timeout](command/amend/timeout.md) as implementation example.