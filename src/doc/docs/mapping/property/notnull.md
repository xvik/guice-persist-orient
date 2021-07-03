# @ONotNull

!!! summary ""
    Scope: property

Marks property as [not null](https://orientdb.com/docs/3.0.x/sql/SQL-Alter-Property.html) (orient scheme marker)

```java
public class MyModel {
    @ONotNull
    private String foo;
}
```

May be used to unset not null marker:

```java
public class MyModel {
    @ONotNull(false)
    private String foo;
}
```
Annotation prefixed to avoid conflicts with `javax.validation.NotNull`.
