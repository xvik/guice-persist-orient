# @Script

!!! summary ""
    Command method extension

Allows you to write small scripts in [sql](https://orientdb.org/docs/3.1.x/sql/SQL-batch.html), [javascript](https://orientdb.org/docs/3.1.x/js/Javascript-Command.html) or any other scripting language.

For example:

```java
@Script("begin" +
  "let account = create vertex Account set name = :name" +
  "let city = select from City where name = :city" +
  "let edge = create edge Lives from $account to $city" +
  "commit retry 100" +
  "return $edge")
Edge linkCity(@Param("name") String name, @Param("city") String city)
```

By default SQL language used for commands.

Example of javascript command:

```java
@Script(language = "javascript", value =
 "for( i = 0; i < 1000; i++ ){" +
     "db.command('insert into Model(name) values (\"test'+i+'\")');" +
 "}")
void jsScript()
```

Note that in some cases script allows you to avoid `OConcurrentModificationException`:

```java
@Script("begin" +
  "update Model set name = :0" +
  "commit")
void update(String name)
```

This may be not the best way in all cases, but it works (due to implementation specifics simple query [may fail in concurrent cases](../../guide/transactions.md#retry)).
Also, note as positional parameter used as named. Script doesn't work with positional parameters, but it works like this.

Internally `OCommandScript` used.
