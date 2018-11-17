# @Function

!!! summary ""
    Command method extension

Function methods execute [function](http://www.orientechnologies.com/docs/last/orientdb.wiki/Functions.html).

For example, suppose we create a function like this:

```sql
CREATE FUNCTION function1 "select from Model" LANGUAGE SQL
```

Now we can call it like this:

```java
@Function("function1")
List<Model> select(String name)
```

Internally OCommandFunction used.
