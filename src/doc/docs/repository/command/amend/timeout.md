# @Timeout

!!! summary ""
    Command method amend extension

Sets command execution timeout and timeout strategy (throw exception or return empty result).

```java
@Query("select from Model")
@Timeout(200)
List<Model> all()
```

If query will not execute in 200 milliseconds, exception will be thrown (by default exception timeout startegy used).

```java
@Query("select from Model")
@Timeout(value = 200, strategy = OCommandContext.TIMEOUT_STRATEGY.RETURN)
List<Model> all()
```

Will return empty (or incomplete) result if query executes longer than 200 milliseconds.

Internally timeout set using `OCommandRequest.setTimeout()` method.