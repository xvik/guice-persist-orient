# @FetchPlan

!!! summary ""
    Command method param extension

Annotates parameter as [fetch plan](https://orientdb.org/docs/3.1.x/java/Fetching-Strategies.html) value. 
This is useful for universal queries to use different fetch plans with different calls.

```java
@Query("select from Model")
List<Model> selectAll(@FetchPlan String plan);
```

Only `String` parameter type may be used. 
Default fetch plan may be specified:

```java
@Query("select from Model")
List<Model> selectAll(@FetchPlan("*:0") String plan);
```

If null value provided as fetch plan and no default set, then no fetch plan will be set.
