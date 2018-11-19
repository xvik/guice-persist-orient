# @Param

!!! summary ""
    Command method param extension

Marks named parameter query parameter. Parameter may be of any type, supported by orient.
For example, if `ODocument` passed, orient will use it as `@rid`. 

```java
@Query("select from Model where name = :name")
List<Model> findByName(@Param("name") String name);
```

!!! tip
    You are not restricted to use only this annotation: you [may write your own extension annotation](../../commandinternals.md#implementing-command-parameter-extension) 
     (e.g. to add some additional validations).
