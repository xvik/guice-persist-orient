# Command methods

The following methods are using orient commands for execution and called command methods.

* [@Query](command/query.md)  - select/update/insert query
* [@Function](command/function.md) - function call
* [@Script](command/script.md) - script call (sql, js etc)
* [@AsyncQuery](command/asyncquery.md) - async query call
* [@LiveQuery](command/livequery.md) - live query subscription

## Common command options

All command methods annotations has two options:

* returnAs - defines required collection implementation
* connection - defines required connection

Most of the time they are not needed.

ReturnAs example:

```java
@Query(value = "select from Model", returnAs = TreeSet.class)
Set<Model> select()
```

Method will return `TreeSet` as result (for example, we want sorted results).

Connection example:

```java
@Query(value = "select from Model", connection = DbType.OBJECT)
List select()
```

Result is not generified, so without hint document connection would be selected for method and result will be
`List<ODocument>`. With connection hint actual result will be `List<Model>`.

Another case, when it might be useful is custom converter. For example, you want to convert results to some DTO object, but your converter converts model objects. If you will not use hint:

```java
@Query("select from Model")
@ModelDTOConverter
List<ModelDTO> select()
```
This will not work, because document connection will be selected and converter expects objects. 
When we set connection hint (`connection = DbType.OBJECT`), everything will work as planned.

!!! note 
    @ModelDTOConverter does not exist, it's just hypothetical result converter extension you could write, using extension api.

## Parameters

Commands support positional and named parameters:

```java
@Query("select from Model where name = ?")
List<Model> positional(String name)

@Query("select from Model where name = :name")
List<Model> named(@Param("name") String name)
```

Positional may be used as named too:
```java
@Query("select from Model where name = :0")
List<Model> positional(String name)
```

For example, script will not work with '?' positional but works which ':0' named-positional.

## El variables

All commands support el variables.

For example:

```java
@Query("select from ${type}")
List selectAll(@ElVar("type") String type);
```

Such variables are inserted into query string before actual execution.
In theory may be used even to provide sql parts, but be careful with it.

By default you can use generics as el variables:

```
select from ${T}
```

where T is generic of query method declaring class (read more about hierarchies below).

Another example is oauth providers connection: suppose you have multiple auth providers and user object has property for each provider id. To avoid writing multiple queries for searching user by provider id, we can do like this:

```java
@Query("select from User where ${provider} = ?")
Optional<User> findByProvider(@ElVar("provider") AuthProvider provider, String providerId);
```

Where AuthProvider is enum:

```java
public enum AuthProvider {
   google, facebook, twitter
}
```

## Command methods parameter annotations

* [@Param](command/param/param.md) - named parameter
* [@ElVar](command/param/elvar.md) - query variable value (substituted in string before query execution)
* [@RidElVar](command/param/ridelvar.md) - extract rid from provided object, document, vertex, string orid and insert into query
* [@Var](command/param/var.md) - orient command variable ($var), may be used by query during execution
* [@Skip and @Limit](command/param/pagination.md) - orient pagination
* [@FetchPlan](command/param/fetchplan.md) - defines fetch plan for query
* [@Listen](command/param/listen.md) - to provide query listener (required for async queries)
* [@DynamicParams](command/param/dynamic.md) - map dynamic count of parameters from array/collection/map

## Command amend annotations

* [@Timeout](command/amend/timeout.md) - defines query timeout and timeout strategy

Amend annotations may be used directly on method, on class (to apply for all methods) or on root repository type (to apply to all inherited mixins).
Command amend methods doesn't affect delegate methods (only if you define command amend annotation directly on delegate method it will cause error, because obviously it's impropriate usage)

## Writing extensions

You can write [custom extensions](commandinternals.md)