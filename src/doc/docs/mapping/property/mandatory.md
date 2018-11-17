# @Mandatory

!!! summary ""
    Scope: property

Marks property as [mandatory](http://orientdb.com/docs/last/SQL-Alter-Property.html) (orient scheme marker)

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