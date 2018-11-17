# @Var

!!! summary ""
    Command method param extension

Marks parameter as [command variable](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Query.html#let-block).

In contrast to el vars, these variables are used during query execution:

For example,

```java
@Query('select name from Model where name in $tst')
String[] findByName(@Var("tst") List tst);
```

Not the best example (could be easily rewritten with sql parameter), but it just demonstrates usage.
