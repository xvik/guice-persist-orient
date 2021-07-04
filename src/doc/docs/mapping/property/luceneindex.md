# @LuceneIndex

!!! summary ""
    Scope: property

Creates [lucene fulltext index](https://orientdb.org/docs/3.1.x/indexing/FullTextIndex.html) for annotated field.

`com.orientechnologies:orientdb-lucene:2.2.33` dependency must be installed to use lucene index.

```java
public class MyModel {
    @LuceneIndex(EnglishAnalyzer)
   private String foo;
}
```

When analyzer is not specified default StandardAnalyzer will be used

If index name not defined it will be created by convention: <class name>.<field name>.
For example above it will be "MyModel.foo"

If index with specified name is registered, and index type is the same then nothing will be done.
If existing index type is lucene with different analyzer - it will be recreated with required analyzer.

If existing index build with different fields or its not lucene index then error will be thrown.  
