# @Var

!!! summary ""
    Command method param extension

Marks parameter as [command variable](https://orientdb.org/docs/3.1.x/sql/SQL-Query.html#let-block).

In contrast to [el vars](elvar.md), these variables are used during query execution:

For example,

```java
@Query('select name from Model where name in $tst')
String[] findByName(@Var("tst") List tst);
```

!!! note
    This is not the best example (could be easily rewritten with sql parameter), but it just demonstrates usage.
