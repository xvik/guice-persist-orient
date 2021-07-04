# @Index

!!! summary ""
    Scope: property

Creates [index](https://orientdb.org/docs/3.1.x/indexing/Indexes.html) for annotated field.

```java
public class MyModel {
   @Index(OClass.INDEX_TYPE.NOTUNIQUE)
   private String foo;
}
```

If index name not defined it will be created by convention: <class name>.<field name>.
For example above it will be "MyModel.foo"

For multiple indexes creation use wrapper:
```java
public class MyModel {
   @Index.List({
            @Index(value = OClass.INDEX_TYPE.NOTUNIQUE, name = "test1"),
            @Index(value = OClass.INDEX_TYPE.FULLTEXT, name = "test2")
    })
   private String bar;
}
```

In this case name is required (only one index may use default name).

Note that index names are global in orient, so define unique names.

If index with specified name is registered, and index type is the same nothing will be done.
If existing index type is different - it will be recreated with required type.

If existing index build with different fields, error will be thrown.

Use [@CaseInsensitive](caseinsensitive.md) on properties to make index case-insensitive.
