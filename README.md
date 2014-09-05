#Guice integration for OrientDB
[![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build Status](http://img.shields.io/travis/xvik/guice-persist-orient.svg?style=flat&branch=master)](https://travis-ci.org/xvik/guice-persist-orient)
[![Coverage Status](https://img.shields.io/coveralls/xvik/guice-persist-orient.svg?style=flat)](https://coveralls.io/r/xvik/guice-persist-orient?branch=master)

### About

[OrientDB](http://www.orientechnologies.com/orientdb/) is document, graph and object database ([see intro](https://www.youtube.com/watch?v=o_7NCiTLVis)).
Underlying format is almost the same for all database types, which allows us to use single database in any way. For example, schema creation and updates
may be performed as object database (jpa style) and graph queries for complex cases. 

Features:
* Integration through [guice-persist](https://github.com/google/guice/wiki/GuicePersist) (UnitOfWork, PersistService, @Transactional, dynamic finders supported)
* Support for [document](http://www.orientechnologies.com/docs/last/orientdb.wiki/Document-Database.html), [object](http://www.orientechnologies.com/docs/last/orientdb.wiki/Object-Database.html) and
[graph](http://www.orientechnologies.com/docs/last/orientdb.wiki/Graph-Database-Tinkerpop.html) databases
* Database types support according to classpath (object and graph db support activated by adding jars to classpath)
* Auto mapping entities in package to db scheme or using classpath scanning to map annotated entities
* Auto db creation
* Hooks for schema migration and data initialization extensions
* All three database types may be used in single unit of work (but each type will use its own transaction)

### Setup

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

[![Download](https://api.bintray.com/packages/vyarus/xvik/guice-persist-orient/images/download.png) ](https://bintray.com/vyarus/xvik/guice-persist-orient/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/guice-persist-orient/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/guice-persist-orient)

Maven:

```xml
<dependency>
<groupId>ru.vyarus</groupId>
<artifactId>guice-persist-orient</artifactId>
<version>1.0.2</version>
<exclusions>
  <exclusion>
      <groupId>com.orientechnologies</groupId>
      <artifactId>orientdb-graphdb</artifactId>
  </exclusion>
  <exclusion>
      <groupId>com.orientechnologies</groupId>
      <artifactId>orientdb-object</artifactId>
  </exclusion>
</exclusions>
</dependency>
```

Gradle:

```groovy
compile ('ru.vyarus:guice-persist-orient:1.0.2'){
    exclude module: 'orientdb-graphdb'
    exclude module: 'orientdb-object'       
}
```

By default, only document database support is enabled. 

Remove exclusions to enable object and graph db support.

NOTE: It's very important for object db to use exact `javassist` version it depends on. If other libraries in 
your classpath use `javassist`, check that newer or older version not appear in classpath.

### Install the Guice module

```java
install(new OrientModule(url, user, password));
```

See [orient documentation](http://www.orientechnologies.com/docs/last/orientdb.wiki/Concepts.html#database-url) for supported db types.
In short:
* `'memory:dbname'` to use in-memory database
* `'plocal:dbname'` to use embedded database (no server required, local fs folder will be used); db name must be local fs path
* `'remote:dbname'` to use remote db (you need to start server to use it)

By default use 'admin/admin' user.

Default transactions configuration may be specified as additional module parameter.
By default, OPTIMISTIC transactions used (use optimistic locking based on object version, same way as hibernate optimistic locking). 
NOTX mode disables transactions. Read more about [transactions in orient](http://www.orientechnologies.com/docs/last/orientdb.wiki/Transactions.html)

For example, to switch off transactions use:

```java
install(new OrientModule(url, user, password)
                .defaultTransactionConfig(new TxConfig(OTransaction.TXTYPE.NOTX));
```

If provided scheme initializers used it's better to specify model package (more details below):

```java
install(new OrientModule(url, user, password)
                .schemeMappingPackage("my.model.package");
```

There are two shortcut modules with predefined scheme initializers from objects:
* `PackageSchemeOrientModule` when all model classes in the same package
* `AutoScanSchemeOrientModule` when model classes spread across application (module use classpath scanning to find `@Persistent` annotated entities)

#### Dynamic finders

To use dynamic finders register finders module:

```java
install(new FinderModule()
            .addFinder(FinderInterface.class));
```

Default connection type could be specified to use when connection type can't be detected from finder return type (by default it will be document connection):

```java
install(new FinderModule()  
            .defaultConnectionType(DbType.OBJECT)
            .addFinder(FinderInterface.class));
```

To avoid manual addition of all finder interfaces, you can use module with classpath scanning instead:

```java
install(new AutoScanFinderModule("package.to.search.finders.in"));
```

### Usage

##### Lifecycle

You need to manually start/stop persist service in your code (because only you can control application lifecycle).
On start connection pools will be initialized, database created (if required) and scheme/data initializers called. Stop will shutdown
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
* `@Transactional` annotation on guice bean or single method (additional `@TxType` annotation allows to define different transaction type for specific unit of work)
* Inject `TxTemplate` or `SpecificTxTemplate` beans into your service and use them

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

`TxTemplate` is a generic template to define unit of work, but you will need to use provider to obtain connection.
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

To obtain connection use one of the following providers:
* `Provider<OObjectDatabaseTx>` for object database connection
* `Provider<ODatabaseDocumentTx>` for document database connection
* `Provider<OrientBaseGraph>` for graph database connection (transactional or not)
* `Provider<OrientGraph>` for transactional graph database connection (will fail if notx transaction type)
* `Provider<OrientGraphNoTx>` for non transactional graph database connection (will provide only for notx transaction type, otherwise fail)

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

### Dynamic finders

Dynamic finders allows to define queries using method annotation and pass parameters from method arguments, e.g.:

```java
@Finder(query = "select from Model where name=? and nick=?")
List<Model> parametersPositional(String name, String nick);
```

If finder used in interface, all interface methods must be finder methods and interface must be manually registered in `FinderModule`.
If `AutoScanFinderModule` used, finder interfaces will be registered automatically. 
`@Transactonal` annotation is supported within interface finders (generally not the best idea to limit transaction to finder method, but in some cases could be suitable)

Finders may be used with beans (e.g. to supplement usual dao methods):

```java
@Finder(query = "select from Model")
public List<Model> selectAll() {
    throw new UnsupportedOperationException("Should be handled with finder interceptor");
}
```

##### Annotations

`@Finder` allows you
* to use [function](http://www.orientechnologies.com/docs/last/orientdb.wiki/Functions.html) with `namedQuery` attribute (there are no named queries in orient, but functions are close concept (but better))
* define query with `query` attribute
* override result collection implementation with `returnAs` attribute

Function call:

```java
@Finder(namedQuery = "function1")
List<Model> function();
```

Query with positional parameters:

```java
@Finder(query = "select from Model where name=? and nick=?")
List<Model> parametersPositional(String name, String nick)
```

Query with named parameters:

```java
@Finder(query = "select from Model where name=:name and nick=:nick")
List<Model> parametersNamed(@Named("name") String name, @Named("nick") String nick)
```

Both `com.google.inject.name.Named` and `javax.inject.Named` annotations supported

Update query example:

```java
@Finder(query = "update Model set name=?")
int updateWithCount(String name)
```

Update query return type could be `void`, `int`, `long`, `Integer` and `Long`.

`@FirstResult` and `@MaxResults` may be used to limit query results ([pagination](http://www.orientechnologies.com/docs/last/orientdb.wiki/Pagination.html)):

```java
@Finder(query = "select from Model where name=? and nick=?")
List<Model> parametersPaged(String name, String nick, @FirstResult int start, @MaxResults int max)
```

You can use `Long`, `Integer`, `long` and `int` for start/max values.
First result is used as orient `SKIP` declaration (you can think of it as first result, counting from 0)

For function call only `@MaxResults` may be used.

`@Use` annotation may be used to specify connection type to use for query:

```java
@Finder(query = "update Model set name='changed'")
@Use(DbType.OBJECT)
void updateUsingObjectConnection()
```

It may be important for proper return type (different connection return different objects: ODocument for document, model instances for object and
Vertex and Edge for graph). For update queries connection type can't be detected and it always executed with default connection 
(document, but may be changed in module configuration), but it may be important to execute it in the same transaction with other changes 
(e.g. if you work in object connection and want update in the same connection).

##### Return types

You can use: `Iterable`, `Collection`, `List`, `Set`, any collection implementation, array, single element or `Iterator`.
Query execution result will be converted in accordance with specified return type.

```java
@Finder(query = "select from Model")
Model selectAll()
```

First result will be returned (query implicitly limited to one element)

```java
@Finder(query = "select from Model")
Model[] selectAll()
```
Returned collection will be converted to array.

Sometimes it may be desirable to change default returned collection implementation, e.g. to sort elements:

```java
@Finder(query = "select from Model", returnAs = TreeSet.class)
Set<Model> selectAll()
```

TreeSet collection will be returned. The same result will be if set method return type to TreeSet 
(but it's not best practice to define implementation as return type).

##### Connection type detection

Connection type can be detected for queries using method return type. 
If you specify generic for returned collection (or iterator) or use typed array (not Object[]) or in case of single element,
connection type will be detected like this:
* If returned class is registered with object entity manager, object connection will be used
* If `ODocument` class returned - document connection used 
* If `Vertex` or `Edge` classes returned - graph connection

But, for example, even if you try to select not object itself, but fields you will get ODocument, even in object connection
(kind of result set in jdbc). For such case document connection will be selected (according to return type), 
but it may be part of object connection logic (e.g. all other queries in object connection). In this case `@Use` annotation will help.

### Schema initialization

Module will automatically create database if it's not exist and perform default graph initialization if graph database support enabled (jar available).

To initialize (or migrate) scheme register implementation of

```java
ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer

```

Example:

```java
install(new OrientModule(url, user, password));
bind(SchemeInitializer.class).to(MySchemeInitializer.class);
```

Scheme initializer is called in notx unit of work (orient requires database schema updates to be performed without transaction).

By default, no-op implementation enabled.

Two default implementations provided for schema initialization from pojos (jpa like):
* `PackageSchemeInitializer` - use all classes in package to init or update scheme (package should be specified as module constructor argument)
* `AutoScanSchemeInitializer` - search classpath for entities annotated with `@Persistent` annotation and use them to create/update scheme 
(search scope may be reduced by specifying package in module constructor).


There are predefined shortcut modules for each initializer: `PackageSchemeOrientModule` and `AutoScanSchemeOrientModule`

Both initializers support grahp compatible entities creation (default orient scheme auto creation used). By default, you will be able just query stored records with graph connection.
To be able to create graph vertixes and edges, classes must extend V and E classes in scheme.

You can use special annotations on entities: 
* `@VertexType` to make your type derive from `V` in scheme
* `@EdgeType` to make your type derive from `E` in scheme

This allows to use objects for scheme update for graphs too. 
You can freely use such annotated entities for object or document queries (difference will be only for graph - it will be able to create).

Remember that scheme created from objects maintain same hierarchy as your objects. E.g. if you use provided `VersionedEntity` class as base
class for entities, it will be also registered in scheme (nothing bad, you may not notice it).
But for graphs hierarchies more important: both vertex and edge objects can't extend same class (root class in hierarchy must extend V or E).
So if you use `@VertexType` or `@EdgeType` annotations make sure their hierarchy not intersect.

### Object mapping specifics

See [orient object mapping documentation](http://www.orientechnologies.com/docs/last/orientdb.wiki/Object-2-Record-Java-Binding.html) for object mapping
 ([and general object database page](http://www.orientechnologies.com/docs/last/orientdb.wiki/Object-Database.html)).

* Orient ignore package, so class may be moved between packages
* When entity field removed, orient will hold all data already stored in records of that field
* When entity field type changes, orient WILL NOT migrate automatically (you need to handle it manually, using custom scheme initializer or through
[orient studio](http://www.orientechnologies.com/docs/last/orientdb-studio.wiki/Home-page.html)).
* When class renamed orient will register it as new entity and you will have to manually migrate all data
(it's possible to use sql commands to rename entity in scheme)
* To use entity within optimistic transaction, it must have version field (annotated with @Version). You should add field manually or extend all entities from 
provided base class: `VersionedEntity`
* JPA annotations can be used to [define cascades](http://www.orientechnologies.com/docs/last/orientdb.wiki/Object-Database.html#cascade-deleting)

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

Orient module initialize connection pool for each database type. Unit of work didn't trigger direct connection acquire from pool
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

Transaction is thread-bound (the same as unit of work).

IF you use more than one connection type within same unit of work and one transaction will fail, other connection will still be committed and rollback will be called
only on failed connection.

You can fine-tune transaction to rollback only in soecific exceptions or not rollback on some exceptions (see @Transactional annotation and TxConfig).

Read more about [orient transactions](http://www.orientechnologies.com/docs/last/orientdb.wiki/Transactions.html)

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

#### Dynamic finders

By analogy with orient module pools, finder module use executor instances for each connection type. 
Executor is responsible for query execution in exact connection. Default executors could be overridden by overriding FinderModule:

```java
public class MyFinderModule extends FinderModule {
    @Override
    protected void configureExecutors() {
          bindExecutor(CustomDocumentFinderExecutor.class);
          bindExecutor(CustomObjectFinderExecutor.class);
          bindExecutor(CustomGraphFinderExecutor.class);
    }      
}
```

Sql command object builder, used by default executors may be overridden:

```java
bind(CommandBuilder.class).to(CustomCommandBuilder.class);
```

Custom query execution result converter may be defined:

```java
bind(ResultConverter.class).to(MyCustomResultConverter.class);
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

Read about [all configuration options](http://www.orientechnologies.com/docs/last/orientdb.wiki/Configuration.html)

### Might also like

* [dropwizard-orient-server](https://github.com/xvik/dropwizard-orient-server) - embedded orientdb server for dropwizard
* [guice-validator](https://github.com/xvik/guice-validator) - hibernate validator integration for guice 
(objects validation, method arguments and return type runtime validation)
* [guice-ext-annotations](https://github.com/xvik/guice-ext-annotations) - @Log, @PostConstruct, @PreDestroy and
utilities for adding new annotations support

-
[![Slush java lib generator](http://img.shields.io/badge/Powered%20by-Slush%20java%20lib%20generator-orange.svg?style=flat-square)](https://github.com/xvik/slush-lib-java)
