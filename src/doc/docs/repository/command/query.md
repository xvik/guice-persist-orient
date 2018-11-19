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
Internally `OSQLSynchQuery` used for selects and `OCommandSQL` for updates and inserts.

Documentation links:

* [SQL](https://orientdb.com/docs/last/SQL.html)
* [functions](https://orientdb.com/docs/last/SQL-Functions.html)
* [methods](https://orientdb.com/docs/last/SQL-Methods.html)
* [fields](https://orientdb.com/docs/last/Document-Field-Part.html)
* [attributes](https://orientdb.com/docs/last/SQL-Where.html#record-attributes)
* [default variables](https://orientdb.com/docs/last/SQL-Where.html#variables)
* [traverse](https://orientdb.com/docs/last/SQL-Traverse.html)
* [query indexes](https://orientdb.com/docs/last/SQL.html#automatic-usage-of-indexes)
