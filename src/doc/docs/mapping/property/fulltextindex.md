# @FulltextIndex

!!! summary ""
    Scope: property

Creates [fulltext index](https://orientdb.org/docs/3.1.x/indexing/FullTextIndex.html) for annotated field.

```java
public class MyModel {
   @FulltextIndex(
            indexRadix = false,
            ignoreChars = "'",
            separatorChars = "!?",
            minWordLength = 5,
            stopWords = ["of", "the"])
   private String foo;
}
```

!!! note 
    Fulltext index may be created with `@Index` annotation too, but this one allows to override default parameters.

When no annotation parameters specified - default values will be used (default in annotation are the same as orient defaults).

If index name not defined it will be created by convention: <class name>.<field name>.
For example above it will be "MyModel.foo"

If index with specified name is registered, and index type and parameters are the same then nothing will be done.
If existing index type is different or parameters are different - it will be recreated with required type or parameters.

If existing index build with different fields or type different from FULLTEXT or FULLTEXT_HASH_INDEX, error will be thrown.  
