# @DropIndexes

!!! summary ""
    Scope: class

Drops existing [indexes](https://orientdb.org/docs/3.1.x/indexing/Indexes.html).

```java
@DropIndexes({"test1", "test2"})
public class MyModel {...}
```

!!! important
    Index drop executed before registration and index creation annotations register indexes after class registration, so drop may be used to re-build index.
