# @Mandatory

!!! summary ""
    Scope: property

Marks property as [mandatory](https://orientdb.com/docs/3.0.x/sql/SQL-Alter-Property.html) (orient scheme marker)

```java
public class MyModel {
    @Mandatory
    private String foo;
}
```

May be used to unset mandatory marker:

```java
public class MyModel {
    @Mandatory(false)
    private String foo;
}
```