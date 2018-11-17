# Delegate method implementation

`@Delegate` method delegates execution to external guice bean method.

It's processing is a bit different from command methods: when target method found, parameter extensions processed on target method parameters! Amend extensions are searched on repository method, type and root repository type.

Delegate methods use `DelegateMethodDescriptor`.

Delegate execution is:

* Prepare arguments array
* Process extension to fill/modify arguments
* Call target method

Delegate amend extension interface `DelegateExtension` allows you to just modify composing arguments array.

Parameters context `DelegateParamsContext` is also simple: supports only ordinal parameters. 
But important moment is context: `paramsContext.getDescriptorContext()` return target method context. It's logical, because param extensions works on target method and so require its context.

Repository method context is also accessible with `paramsContext.getCallerContext()`.

## Implementing delegate parameter extension

!!! note
    Assuming you already read [command methods extension guide](commandinternals.md), some details omitted.

```java
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(CustomParamExtension.class)
public @interface CustomParam { ... }
```

```java
@Singleton
public class ConnectionParamExtension implements MethodParamExtension<DelegateMethodDescriptor,
        DelegateParamsContext, CustomParam>, DelegateExtension<DelegateMethodDescriptor> {

    @Override
    public void processParameters(final DelegateMethodDescriptor descriptor,
                                  final DelegateParamsContext context,
                                  final List<ParamInfo<CustomParam>> paramsInfo) {
        ...
    }

    @Override
    public void amendParameters(final DelegateMethodDescriptor descriptor, final Object[] targetArgs,
                                final Object instance, final Object... arguments) {
        ...
    }    
```

!!! note 
    `DelegateMethodDescriptor` used in generic, which limit extension usage for delegates only.

Usually, delegate param extension without amend extension interface makes no sense, because values for extended parameters will not be populated by default (these parameters extends repository method signature, so its simply impossible to populate its values automatically). 

As with command extensions, use KEY field and custom descriptor to pass values between parsing and execution phases. 
ParamInfo contains target method parameter position, which you will use to set computed value to arguments array for target method.

## Implementing delegate amend extensions

As with command, amend extension may be registered globally or defined as annotation on repository method. 
All steps are the same as with command, so will omit description.

Delegate amend extension could just modify arguments, and I dont know any good usage example for this (that's why no bundled amend annotations provided for delegates: usually, param extensions are more than enough).