# @Mandatory

!!! summary ""
    Scope: property

Marks property as [mandatory](https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Property.html) (orient scheme marker)

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