# @DetachResult

Results detaching may be used only with object connection (will be always the case when expected result is model class (or collection of model classes)).

```java
@Query("select from Model")
@DetachResult
List<Model> selectAll()
```

This may be important if you use small transaction scopes, e.g. if method above represent transaction scope,
then if we will call it without detach converter, then any attempt to access list object field will fail:
returned proxies doesn't work without transaction.

!!! note 
    Used detach method will completely unproxy entire object tree. If for example, you use remote connection
    with fetch plan, then detach will load entire graph during detaching.

In most cases detach should be not needed, because most likely entire business logic will be covered with transaction.

You can use this extension as reference for writing your own [conversion extension](../internals.md#result-converter-extensions).