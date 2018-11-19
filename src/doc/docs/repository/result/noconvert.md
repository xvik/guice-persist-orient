# @NoConversion

Disables any [result conversion](../result.md). 

!!! note 
    By default special converter applied globaly handles many cases
    like collection reducing to single element, projections etc.

For example:

```java
@Query("select from Model")
@NoConversion
Model selectAll()
```

This query will fail to execute, because of class cast exception: returned `List<Model>` can't be casted to `Model`.
