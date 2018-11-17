# @Query

!!! summary ""
    Command method extension

Query methods used for queries (select/update/insert).

Select query:

```java
@Query("select from Model where name=?")
List<Model> select(String name)
```

Update query:

```java
@Query("update Model set name = ? where name = ?")
int update(String to, String from)
```
Update query return type could be `void`, `int`, `long`, `Integer` and `Long`.

Insert query:

```java
@Query("insert into Model (name) values (?)")
Model insert(String name)
```
Internally OSQLSynchQuery used for selects and OCommandSQL for updates and inserts.

Documentation links:
* [SQL](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL.html)
* [functions](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Functions.html)
* [methods](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Methods.html)
* [fields](http://www.orientechnologies.com/docs/last/orientdb.wiki/Document-Field-Part.html)
* [attributes](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Where.html#record-attributes)
* [default variables](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Where.html#variables)
* [traverse](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Traverse.html)
* [query indexes](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL.html#automatic-usage-of-indexes)
