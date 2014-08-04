#Guice integration for OrientDB
[![Build Status](https://travis-ci.org/xvik/guice-persist-orient.svg?branch=master)](https://travis-ci.org/xvik/guice-persist-orient)
 [ ![Download](https://api.bintray.com/packages/vyarus/xvik/guice-persist-orient/images/download.png) ](https://bintray.com/vyarus/xvik/guice-persist-orient/_latestVersion)

### About

[OrientDB](http://www.orientechnologies.com/orientdb/) is document, graph and object database ([see intro](https://www.youtube.com/watch?v=o_7NCiTLVis)).
Underlying format is almost the same for all database types, which allows to use single database in any way. For example, schema creation and updates
may be performed as object database (jpa style) and in complex cases use graph queries. 

Features:
* Integration through [guice-persist](https://github.com/google/guice/wiki/GuicePersist) (UnitOfWork, PersistService, @Transactional annotation supported)
* Support for [document](https://github.com/orientechnologies/orientdb/wiki/Document-Database), [object](https://github.com/orientechnologies/orientdb/wiki/Object-Database) and
[graph](https://github.com/orientechnologies/orientdb/wiki/Graph-Database-Tinkerpop) databases
* Database types support according to classpath (object and graph db support activated by adding jars to classpath)
* Auto mapping entities in package to db scheme or using classpath scanning to map annotated entities
* Auto db creation
* Hooks for schema migration and data initialization extensions
* All three database types may be used in single unit of work (but each type will use its own transaction)

### Setup

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

Gradle:

```groovy
compile ('ru.vyarus:guice-persist-orient:0.9.0'){
    // gradle includes optional dependencies.. fixing
    exclude module: 'orientdb-graphdb'
    exclude module: 'orientdb-object'       
}
```

By default, only document database support is enabled. 

To add object database support:

```groovy
compile 'com.orientechnologies:orientdb-object:1.7.7'
```

NOTE: It's very important for object db to use exact javassist version it depends on. If other libraries in 
your classpath use javassist, check that newer or older version not appear in classpath.

To add graph database support:

```groovy
compile 'com.orientechnologies:orientdb-graphdb:1.7.7'
```

### Install the Guice module

```java
install(new OrientModule(url, user, password));
```

See [orient documentation](https://github.com/orientechnologies/orientdb/wiki/Concepts#database_url) for supported db types.
In short:
* 'memory:dbname' to use in-memory database
* 'plocal:dbname' to use embedded database (no server required, local fs folder will be used); db name must be local fs path
* 'remote:dbname' to use remote db (you need to start server to use it)

By default use 'admin/admin' user.

Default transactions configuration may be specified as additional module parameter.
By default, OPTIMISTIC transactions used (use optimistic locking based on object version, same way as hibernate optimistic locking). 
NOTX mode disables transactions. Read more about [transactions in orient](https://github.com/orientechnologies/orientdb/wiki/Transactions)

For example, to switch off transactions use:

```java
install(new OrientModule(url, user, password, null, new TxConfig(OTransaction.TXTYPE.NOTX));
```

### Usage

##### Lifecycle

You need to manually start/stop persist service in your code (because only you can control application lifecycle).
On start connection pools will be initialized, database created (if required) and schema/data updaters called. Stop will shutdown
all connection pools.

```java
@Inject
PersistService orientService

public void onAppStartup(){
    orientService.start()
}

public void onAppShutdown(){
    orientService.stop()
}
```

##### Unit of work (transaction)

To define unit of work use: 
* @Transactional annotation on guice bean or single method (additional @TxType annotation allows to define different transaction type for specific unit of work)
* Inject TxTemplate or SpecificTxTemplate beans into your service and use them

Example of no tx unit of work with annotation:

```java
@Transactional
@TxType(OTransaction.TXTYPE.NOTX)
public void method()
```

TxTemplate example:

```java
txTemplate.doInTransaction(new TxAction<Void>() {
        @Override
        public Void execute() throws Throwable {
            // something
            return null;
        }
    });
```

TxTemplate with custom config:

```java
txTemplate.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), new TxAction<Void>() {
        @Override
        public Void execute() throws Throwable {
            // something
            return null;
        }
    });
```

TxTemplate is a generic template to define unit of work, but you will need to use provider to obtain connection.
If you need only specific connection type, use specific template:

```java
specificTxTemplate.doInTransaction(new SpecificTxAction<Object, OObjectDatabaseTx>() {
        @Override
        public Object execute(OObjectDatabaseTx db) throws Throwable {
            // something
            return null;
        }
    })
```

To obtain connection use one of providers:
* Provider&lt;OObjectDatabaseTx&gt; for object database connection
* Provider&lt;ODatabaseDocumentTx&gt; for document database connection
* Provider&lt;OrientBaseGraph&gt; for graph database connection (transactional or not)
* Provider&lt;OrientGraph&gt; for transactional graph database connection (will fail if notx transaction type)
* Provider&lt;OrientGraphNoTx&gt; for non transactional graph database connection (will provide only for notx transaction type, otherwise fail)


### Schema initialization

Module will automatically create database if it's not exist and perform default graph initialization if graph database support enabled (jar available).

To initialize (or migrate) scheme register implementation of

```java
ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer
```

By default, no-op implementation enabled.

Two default implementations provided for schema initialization from pojos (jpa like):
* PackageSchemeInitializer - use all classes in package to init or update scheme (package should be specified as module constructor argument)
* AutoScanSchemeInitializer - search classpath for entities annotated with @Persistent annotation and use them to create/update scheme 
(search scope may be reduced by specifying package in module constructor).

Example:

```java
install(new OrientModule(url, user, password, null, 'your.package.model');
bind(SchemeInitializer.class).to(PackageSchemeInitializer.class);
```

Also, there are predefined modules for each initializer:
* PackageSchemeOrientModule
* AutoScanSchemeOrientModule

Scheme initializer is called in notx unit of work (orient requires database schema updates to be performed without transaction).

### Object mapping specifics

See [orient object mapping documentation](https://github.com/orientechnologies/orientdb/wiki/Object-2-Record-Java-Binding) for object mapping
 ([and general object database page](https://github.com/orientechnologies/orientdb/wiki/Object-Database)).

* Orient ignore package, so class may be moved between packages
* When entity field removed, orient will hold all data already stored in records of that field
* When entity field type changes, orient WILL NOT migrate automatically (you need to handle it manually, using custom scheme initializer or through
[orient studio](https://github.com/orientechnologies/orientdb-studio/wiki)).
* When class renamed orient will register it as new entity and you will have to manually migrate all data
(it's possible to use sql commands to rename entity in scheme)
* To use entity within optimistic transaction, it must have version field (annotated with @Version). You should add field manually or extend all entities from 
provided base class: VersionedEntity
* JPA annotations can be used to [define cascades](https://github.com/orientechnologies/orientdb/wiki/Object-Database#cascade-deleting)

### Data initialization

To initialize (or migrate) data register implementation of

```java
ru.vyarus.guice.persist.orient.db.data.DataInitializer
```

By default, no-op implementation enabled.

Example:

```java
bind(DataInitializer.class).to(YourDataInitializerImpl.class);
```

Initializer is called WITHOUT predefined unit of work, because of different possible requirements.
You should define unit of work (maybe more than one) yourself (using annotation or template).

### Under the hood

Module initialize connection pool for each database type. Unit of work didn't trigger direct connection acquire from pool
to not acquire redundant resources (for example, if all three database types initialized, most likely you will use one or two 
during single unit of work).

To configure pool sizes use global configuration (default is min 1 max 20):

```java
OGlobalConfiguration.DB_POOL_MIN.setValue(1)
OGlobalConfiguration.DB_POOL_MAX.setValue(20)
```

Each pool (each connection type) will start it's own independent transaction within unit of work. It should not be limitation 
with proper scopes definition (use one connection type just for reads and other for updates or update different entities within 
different connection types or define smaller units of work). Each pool acquires connection and opens transaction only after
requesting connection through appropriate provider.

NOTE: in contrast to spring default proxies, in guice when you call bean method inside the same bean, annotation interceptor will still work.
So it's possible to define few units of work withing single bean using annotations:

```java
public void nonTxMethod(){
    // all called methods are annotated
    doTx1();
    doTx2();
    doTx3();
}
```

Transaction is thread-bound (the same as unit of work).

IF you use more than one connection type within same unit of work and one transaction will fail, other connection will still be committed and rollback will be called
only on failed connection.

You can fine-tune transaction to rollback only in soecific exceptions or not rollback on some exceptions (see @Transactional annotation and TxConfig).

Read more about [orient transactions](https://github.com/orientechnologies/orientdb/wiki/Transactions)

Default transaction manager implementation could be overridden by simply registering different implementation of TransactionManager interface:

```java
bind(TransactionManager.class).to(CustomTransactionManagerImpl.class)
```

Pools implementation could also be changed. For example, current graph pool is actually document pool (because graph api 'sits' above document connection).
So it is possible to share document and graph connections: just implement graph pool to acquire connection from document pool.
It wasn't done, because of inconsistency: document and object transactions would be different and graph will share document transaction.. a bit confusing.

Custom pools registration example:

```java
public class MyOrientModule extends OrientModule {

    @Override
    protected void configurePools() {
        bindPool(ODatabaseDocumentTx.class, DocumentPool.class);
        bindPool(OObjectDatabaseTx.class, ObjectPool.class);
        bindPool(OrientGraph.class, MyCustomGraphPool.class);
        // note that for graph few entities could be provided: OrientGraph, OrientGraphNoTx, OrientBaseGraph.
        // default implementation registers additional providers to handle all cases
        // see ru.vyarus.guice.persist.orient.support.pool.GraphPoolBinder
    }
}
```

### Orient configuration

Configuration could be done on instance: 

```java
db.getStorage().getConfiguration()
```

Or globally: 

```java
OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
```

Read about [all configuration options](https://github.com/orientechnologies/orientdb/wiki/Configuration)

### Licence

MIT