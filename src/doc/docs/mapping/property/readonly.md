# @Readonly

!!! summary ""
    Scope: property

Marks property as [readonly](http://orientdb.com/docs/last/SQL-Alter-Property.html) (orient scheme marker)

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
