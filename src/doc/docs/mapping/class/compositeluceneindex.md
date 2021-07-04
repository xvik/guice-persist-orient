# @CompositeLuceneIndex

!!! summary ""
    Scope: class

Creates composite [lucene fulltext index](https://orientdb.org/docs/3.1.x/indexing/FullTextIndex.html) (index span multiple properties).

`com.orientechnologies:orientdb-lucene:2.2.33` dependency must be installed to use lucene index.

```java
@CompositeLuceneIndex(name = "test", fields = ["foo", "bar"], analyzer = EnglishAnalyzer)
public class MyModel {...}
```

For multiple indexes creation use wrapper:
```java
@CompositeIndex.List({
   @CompositeLuceneIndex(name = "test", fields = ["foo", "bar"])
   @CompositeLuceneIndex(name = "test2", fields = ["foo", "bar"], analyzer = EnglishAnalyzer)
})
public class MyModel {...}
```

!!! note
    Index names are global in orient, so define unique names.

If index with specified name is registered, and index type is the same nothing will be done.
If existing index created with different analyzer - it will be recreated with required analyzer.

If existing index build with different fields or it's not lucene index, error will be thrown.
