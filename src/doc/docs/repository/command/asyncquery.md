# @AsyncQuery

!!! summary ""
    Command method extension

Executes query [asynchronously](https://orientdb.org/docs/3.1.x/java/Document-API-Documents.html#asynchronous-queries). 
By default, async query execution is blocking: method is blocking while listener is called (and listener is executed at the same thread). Such query is useful for dynamic filtering: results are analyzed one by one and you can manually stop further results processing.

Example: 

```java
@AsyncQuery("select from Model")
void select(@Listen OCommandResultListener listener)
```

Returned result will be passed to the provided listener (always as `ODocument`).

## Listener

Special listener type could be used to automatically convert the provided document (the same way as repository return result is converted):

```java
@AsyncQuery("select from Model")
void select(@Listen AsyncQueryListener<Model> listener)
```

Projection will also work:

```java
@AsyncQuery("select name from Model")
void selectProjection(@Listen AsyncQueryListener<String> listener)
```

And even conversion to graph api:

```java
@AsyncQuery("select from VertexModel")
void selectVertex(@Listen AsyncQueryListener<Vertex> listener)
```

## Non blocking

Non blocking async query is executed truly asynchronously: listener called in a separate thread. Non blocking query may return future.

```java
@AsyncQuery(value = "select from Model", blocking = false)
Future<List<Model>> selectNonBlock(@Listen AsyncQueryListener<Model> listener)
```

Future may be used to wait for the result:

```java
// most likely get will be called somewhere later in code and not directly after async method call
List<Model> result = selectNonBlock().get()
```

Listener execution is wrapped with external transacton, so guice can use the same connection instance as orient in current thread. But it is highly recommended to avoid database operations inside listener because listener must execute as fast as possible (orient recommendation).

Internally `OSQLAsynchQuery` or `OSQLNonBlockingQuery` used accordingly.
