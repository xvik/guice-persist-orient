# @Readonly

!!! summary ""
    Scope: property

Marks property as [readonly](https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Property.html) (orient scheme marker)

```java
public class MyModel {
    @Readonly
    private String foo;
}
```

May be used to unset readonly marker:

```java
public class MyModel {
    @Readonly(false)
    private String foo;
}
```
