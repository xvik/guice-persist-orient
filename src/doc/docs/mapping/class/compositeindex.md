# @CompositeIndex 

!!! summary ""
    Scope: class

Creates composite [index](https://orientdb.org/docs/3.1.x/indexing/Indexes.html) (index span multiple properties).

```java
@CompositeIndex(name = "test", type = OClass.INDEX_TYPE.NOTUNIQUE, fields = ["foo", "bar"])
public class MyModel {...}
```

For multiple indexes creation use wrapper:

```java
@CompositeIndex.List({
   @CompositeIndex(name = "test", type = OClass.INDEX_TYPE.NOTUNIQUE, fields = ["foo", "bar"])
   @CompositeIndex(name = "test2", type = OClass.INDEX_TYPE.DICTIONARY, fields = ["foo", "bar"])
})
public class MyModel {...}
```

Note that index names are global in orient, so define unique names.

If index with specified name is registered, and index type is the same nothing will be done.
If existing index type is different - it will be recreated with required type.

If existing index build with different fields, error will be thrown.
