# @Skip and @Limit 

!!! summary ""
    Command method param extension

Annotates [pagination](http://www.orientechnologies.com/docs/last/orientdb.wiki/Pagination.html) parameters (of course, may be used separately). 

```java
@Query("select from Model")
List<Model> getAll(@Skip int skip, @Limit int limit);
```

Parameter type may be any Number type (Integer, Long etc)

See bundled `Pagination` mixin as usage example.
