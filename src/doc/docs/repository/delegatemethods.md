# Delegate methods

Delegate methods delegate execution to other guice bean method. 

```java
@Delegate(TargetBean.class)
List<Model> selectSomething();
```

On execution, delegating method will be found in `TargetBean` and executed. 
This allows writing custom logic using java api, but still use interface repostory method to call it. So repository interface become single point for all your entity's methods, whereas actual implementation could be decomposed by multiple beans.

`@Delegate` may be declared directly on method or on class to apply for all methods. 

Delegate method implementation may be generic, using some information from calling repository in runtime (generic values, connection object selected for repository method or repository instance itself to use its methods in bean logic).

As with other repository methods, delegate bean method execution result will be converted with default (or custom) [converter](result.md).

Delegate annotation support `returnAs` and `connection` parameters, the same way as [command](commandmethods.md) annotations.

!!! tip
    When writing mixin, prefer implementing mixin interface in delegate bean. This will make strong reference between them (easier to find). If extension annotations used, and you can't directly implement method, use abstract class (with @ProvidedBy(DynamicSingletonProvider.class)). This way you will keep connection between interface and implementation and be able to use extended method signature.
    
    For example,
    ```java
    public interface MyMixin {
       
        @Delegate(OtherBean.class)
        void something();
    }
    
    public abstract class OtherBean implements MyMixin {               
        void something(@Repository Object repo);
    }
    ```
    Here delegation target use custom parameters and so can't mark method as `@Override`, 
    but overall class implmenets mixin interface, declaring explicit connection between them.
    

## Method lookup algorithm

`@Delegate` annotation allows you to define

* target implementation type with `value` attribute
* exact target method name with `method` attribute (this must be used as last resort, because it introduce weak contract and not refactor-friendly)

Method is searched through all target bean methods (including inherited methods). 

Algorithm:

* If method name set directly (annotation method attribute), look only methods with this name. If method name not set look all public methods.
* Check all methods for parameter compatibility. Target method must have compatible parameters
at the same order(!) Special parameters (extension annotations) may appear at any position (before/after/between).
* If more than one method found, repository method name used to reduce results (this should be the most useful hint)
* Method with special parameters (extension annotations) is prioritized. So if few methods found but only one use extensions - it will be chosen.
* Next, methods are filtered by most specific parameters (e.g. two methods with the same name but one declares
String parameter and other Object; first one will be chosen as more specific).
* If we still have more than one possibility, error will be thrown.
 
## Delegate parameter annotations

* [@Generic](delegate/param/generic.md) - generic type value of caller repository (exact class could be specified where to search generic)
* [@Repository](delegate/param/repository.md) - caller repository instance
* [@Connection](delegate/param/connection.md) - db connection object, selected by repository method
