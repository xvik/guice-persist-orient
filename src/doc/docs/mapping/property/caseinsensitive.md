# @CaseInsensitive

!!! summary ""
    Scope: property

Marks property as case insensitive. Case insensitive properties ([collate ci](https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Property.html)) 
are matched in case insensitive way:

```sql
select from MyModel where name = 'Test'
```

If property `name` is case insensitive then record with name `test` (for example) will be matched.

```java
public class MyModel {
    @CaseInsensitive
    private String foo;
}
```

May be used to unset ci marker:

```java
public class MyModel {
    @CaseInsensitive(false)
    private String foo;
}
```

Marking property ci also affects indexes, created on this property - they are also become ci.
In orient ci marker for index and property are different, but if property is ci - index become ci and if 
index marked as ci then quite possible it will be used when you query by this property - so it may seem that property is ci too. 
Always marking property as ci (with annotation) removes confusion and grants more consistent behaviour.

So when you need case-insensitive index use both annotations:

```java
public class MyModel {
    @CaseInsensitive
    @Index(...)
    private String foo;
}
```