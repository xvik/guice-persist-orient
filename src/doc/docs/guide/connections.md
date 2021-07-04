# Connections

[Document](https://orientdb.org/docs/3.1.x/java/Document-Database.html) (actually [multi-model](https://orientdb.org/docs/3.1.x/java/Java-MultiModel-API.html))  is the core connection type. 
[Object](https://orientdb.org/docs/3.1.x/java/Object-Database.html) and [graph](https://orientdb.org/docs/3.1.x/java/Graph-Database-Tinkerpop.html) 
apis use document connection internally.
Connection object mainly defines the result of queries: 

* document connection will always return ODocument
* object connection returns mapped pojos (actually proxies) or ODocument for fields selections and
* graph api returns Vertex and Edge types.

And of course connections provide specific apis for types.

You can use any connections within single transaction and changes made in one connection type will be visible
in other connections. This allows you, for example to update data using object api and create relations using graph api.

To access connection object inside transaction use `PersistentContext` generified with the type of required connection.

* `PersistentContext<ODatabaseObject>` for object database connection
* `PersistentContext<ODatabaseDocument>` for document database connection
* `PersistentContext<OrientBaseGraph>` for graph database connection (transactional or not)
* `PersistentContext<OrientGraph>` for transactional graph database connection (will fail if notx transaction type)
* `PersistentContext<OrientGraphNoTx>` for non transactional graph database connection (will provide only for notx transaction type, otherwise fail)

!!! note 
    You can't use both OrientGraph and OrientGraphNoTx in the same transaction (type must be used according to transaction type).
    OrientBaseGraph may be used in places where both types are possible (its the base class for both).

!!! note 
    `PersistentContext` methods are shortcuts for low level api (simplifies usage). You can extend it to add more shortcut methods
    or make your own: it is not used internally and exists only for public usage.

For example

```java
public class MyService {
    
    @Inject    
    private PersistenceContext<ODatabaseObject> context;
    
    public List<Model> findByName(final String name) {
        // manual transaction declaration
        return context.doInTransaction((db) -> 
            // pure orient api
            db.query(new OSQLSynchQuery<Model>("select from Model where name=?"), name)
        );
    }
}
```    

Alternatively, you can directly inject connections:

```java
@Inject
private Provider<ODatabaseObject> db;
```

But note that it would not work without external transaction.

## Manual connections

In special cases you can inject internal [`OrientDB` object](https://orientdb.org/docs/3.1.x/java/ref/OrientDB.html), 
used for connections management directly:

```java
@Inject Provider<OrientDB> orient;
```

With it, you can create or drop databases and create manual connections, not managed with guice.

!!! note
    Database credentials are accessible (if required) with injectable `OrientDBFactory` bean.

## Pools

Each connection type is managed with its own `PoolManager`.

Default pool implementations:

* `DocumentPool`
* `ObjectPool`
* `GraphPool`

Pools are registered in `OrientModule`'s `configurePools()` method. You can override it to register your own pool implementations.

Custom pools registration example:

```java
public class MyOrientModule extends OrientModule {

    @Override
    protected void configurePools() {
        bindPool(ODatabaseDocument.class, DocumentPool.class);
        bindPool(ODatabaseObject.class, ObjectPool.class);
        bindPool(OrientGraph.class, MyCustomGraphPool.class);
        // note that for graph few entities could be provided: OrientGraph, OrientGraphNoTx, OrientBaseGraph.
        // default implementation registers additional providers to handle all cases
        // see ru.vyarus.guice.persist.orient.support.pool.GraphPoolBinder
    }
}
```

Default pool implementation maintains only pool for documents (using `ODatabasePool`).
Other pools use document connection to construct object and graph connection objects.
This merges different connections transactions (change in one connection type will be visible in all others).

!!! important
    Connection may be acquired from pool only inside unit of work. Connection object is bound to thread local inside pool, returning always the same instance during transaction.

Actual orient transaction is started only when connection object is obtained from pool.

### Pools interactions

Before v3, each pool manage its own connection, as a result transactions were different for all connection types and changes were not visible between them. 

Such behavior is still possible: write new pool implementations and register them in OrientModule.

`TransactionManager` supports multiple transactions out of the box (legacy behavior). 

!!! note 
    `PoolManager`'s commit or rollback methods will be called on each transaction end, even if no connection where obtained from this pool. Pool must control such situation.

When explicit rollback called, transaction manager will call rollback on each pool.

When exception occurred on commit, other pools will be still committed and rollback called only on failed pool.
But other pools will receive rollback call after commit and must ignore it.

If you really want to use multi-transactional approach look sources for 2.x brach.
