# @RidElVar

!!! summary ""
    Command method param extension

A special [el variable](../../commandmethods.md#el-variables) to extract rid from provided object, document, vertex, ORID (object or string) and set as el variable.

It is implemented as el variable and not as parameter because current orient sql parser works not so well in some cases. For example, query like `create edge from ? to ?` will not work with parameters (named too) and the only way to make it work is to embed rids directly into query `create edge from #12:1 to #12:10`.

```java
@Query('select from (traverse out from ${id})')
List<Model> string(@RidElVar("id") String id)

@Query('create edge MyEdge from ${from} to ${to}')
void createEdge(@RidElVar("from") Object from, @RidElVar("to") Object to)
```

!!! note 
    From the first example it looks like `@RidElVar` could be replaced with simple [@ElVar](elvar.md), but it's not: `@ElVar` 
    will complain on string parameter because it's not safe for injection. `@RidElVar` always validate that provided string is valid rid (e.g. #12:11) and fail if string is not (guards from injection).

You may use any supported type as variable type: `ODocument`, object model type, `ORID`, `String`, `Vertex`, `Edge`. 
By using exact type you can restrict method contract. Or use `Object` to accept all possible values (simplify usage when multiple apis used).

Also, may be used for collections, arrays (or varargs). Such types will be inserted as "[rid1, rid2, rid3]" into query.

```java
@Query("select from (traverse out from ${ids})")
public List doSmth(@RidElVar("ids") List<ODocument> ids)

@Query("select from (traverse out from ${ids})")
public List doSmth(@RidElVar("ids") Vertex... ids)
```

Objects inside collection may be of any supported type (even different objects).
