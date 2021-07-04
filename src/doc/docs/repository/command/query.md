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

* [SQL](https://orientdb.org/docs/3.1.x/sql/)
* [functions](https://orientdb.org/docs/3.1.x/sql/SQL-Functions.html)
* [methods](https://orientdb.org/docs/3.1.x/sql/SQL-Methods.html)
* [document api](https://orientdb.org/docs/3.1.x/java/Document-Database.html)
* [attributes](https://orientdb.org/docs/3.1.x/sql/SQL-Where.html#record-attributes)
* [default variables](https://orientdb.org/docs/3.1.x/sql/SQL-Where.html#variables)
* [traverse](https://orientdb.org/docs/3.1.x/sql/SQL-Traverse.html)
* [query indexes](https://orientdb.org/docs/3.1.x/sql/SQL-Introduction.html#automatic-usage-of-indexes)
