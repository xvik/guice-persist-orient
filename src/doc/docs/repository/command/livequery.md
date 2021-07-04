# @LiveQuery

!!! summary ""
    Command method extension

Subscribe listener to orient [live query](https://orientdb.org/docs/3.1.x/java/Live-Query.html).

Live query may use row orient listener interface:

```java
@LiveQuery("select from Model")
int subscribe(@Listen OLiveResultListener listener)
```

!!! note  
    Live query must start with "live", but this is optional as annotation already declares query as live. Anyway, you can write "live select from Model" if you want.

Subscription call will return subscription token, which may be used to unsubscribe query:

```java
@Query("live unsubscribe ${token}")
void unsubscribe(@ElVar("token") int token)
```

Special live result listener may be used with automatic conversions support (much like repository method result conversions):

```java
@LiveQuery("select from Model")
int subscribe(@Listen LiveQueryListener<Model> listener)
```

Graph api could also be used:

```java
@LiveQuery("select from VertexModel")
int subscribeVertex(@Listen LiveQueryListener<Vertex> listener)
```

Of course, pure document is allowed too. But note, that projections will not work here as live query always return entity.

!!! important
    Listener execution is wrapped with external transaction, so guice can use the same connection instance as orient in current thread.

`OLiveQuery` used for query execution.