# Result handling

## Connection type detection

It is very important to define complete return type, because connection type is detected from method return type. 
If you specify generic for returned collection (or iterator) or use typed array (not `Object[]`) or in case of single element, connection type will be detected like this:

* If returned class is registered with object entity manager, object connection will be used
* If `ODocument` class returned - document connection used 
* If `Vertex` or `Edge` classes returned - graph connection

Connection type directly affects produced objects: the same query will return different result objects, executed with different connections.

But, for example, even if you try to select not object itself, but fields you will get `ODocument`, even in object connection (kind of result set in jdbc). For such case document connection will be selected (according to return type).

## Return types

You can use: `Iterable`, `Collection`, `List`, `Set`, any collection implementation, array, single element or `Iterator` as return type.
Single elements (single object return) may be wrapped with `Optional` (guava (`com.google.common.base.Optional`) 
or jdk8 (`java.util.Optional`)). Collections should not be wrapped with optional, because repository method should never return null for collection or array. 

Query execution result will be converted in accordance with specified return type.

!!! note 
    Examples below use [command](commandmethods.md) methods, but conversion mechanism is applied to all kinds of methods including [delegates](delegatemethods.md).

For example: 
```java
@Query("select from Model")
Model selectAll()
```

Returns `List<Model>`, but converter will take just first element (or null if empty) and return just it.

Here first result of returned list is wrapped with optional by converter:

```java
@Query("select from Model")
Optional<Model> selectAll()
```

The same for array (returned list will be converted to array):

```java
@Query("select from Model")
Model[] selectAll()
```

Sometimes it may be desirable to change default returned collection implementation, e.g. to sort elements:

```java
@Query("select from Model", returnAs = TreeSet.class)
Set<Model> selectAll()
```

`TreeSet` collection will be returned. The same result will be if set method return type to `TreeSet` 
(but it's not best practice to define implementation as return type).

## Result projection

In orient, when you query for some aggregated function (like count) or selecting just one field, `ODocument`
or `Vertex` objects will be returned (for document/object and graph connections). This is usually not the desired behavior.

Projection is unwrapping from document or vertex if it contains just one property. Unwrapping is triggered by return type, e.g.

```java
@Query("select count(@rid) from Model")
int getCount();
```

Here return type is int, but actual query will return `ODocument`. Result converter will detect this, look that document contains just one field (count) and return just this field value. 
Note that actual field value could be long or double, conversion to int will also be performed automatically.
If return type would be `ODocument` - no conversion will occur.

When we need just one field from multiple rows:

```java
@Query("select name from Model")
String[] getNamesArray();
```

Query returns collection of `ODocument`, but result converter will look return type and unwrap documents returning simple array.

Projection detection implemented without possible check overhead and so projection may be used with collections too:

```java
@Query("select name from Model")
List<String> getNamesArray();
```

For graph connection this will also work:

```java
@Query(value = "select name from Model", connection=DbType.GRAPH)
String[] getNamesArray();
```

This time orient will return `Vertex` instances and result converter will look if vertex contains just one property and unwrap single value.

Special case: by default, result converter took first collection element if single result required. So projection may be used like this:

```java
@Query("select name from Model")
String getNamesArray();
```

Here collection reduced to one element and single element projected to string value.

## Default converter override

Default converter logic may be overridden in guice module, by simply registering new implementation:

```java
bind(ResultConverter.class).to(CustomResultConverter.class)
```

## Result extensions

Out of the box two extensions provided:

* [@NoConversion](result/noconvert.md) - disables any conversion
* [@DetachResult](result/detach.md) - detaches objects (in object connection proxies will be returned, which will not work outside of transaction).
