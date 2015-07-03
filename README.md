#Guice integration for OrientDB
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/xvik/guice-persist-orient)
[![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build Status](http://img.shields.io/travis/xvik/guice-persist-orient.svg?style=flat&branch=master)](https://travis-ci.org/xvik/guice-persist-orient)
[![Coverage Status](https://img.shields.io/coveralls/xvik/guice-persist-orient.svg?style=flat)](https://coveralls.io/r/xvik/guice-persist-orient?branch=master)

### About

[OrientDB](http://www.orientechnologies.com/orientdb/) is document, graph and object database (see [intro](https://www.youtube.com/watch?v=o_7NCiTLVis) and [starter course](http://www.orientechnologies.com/getting-started/)).
Underlying format is almost the same for all database types, which allows us to use single database in any way. For example, schema creation and updates
may be performed as object database (jpa style) and graph api may be used for creating relations.

Features:
* For orient 2.x
* Integration through [guice-persist](https://github.com/google/guice/wiki/GuicePersist) (UnitOfWork, PersistService, @Transactional)
* Support for [document](http://www.orientechnologies.com/docs/last/orientdb.wiki/Document-Database.html), [object](http://www.orientechnologies.com/docs/last/orientdb.wiki/Object-Database.html) and
[graph](http://www.orientechnologies.com/docs/last/orientdb.wiki/Graph-Database-Tinkerpop.html) databases
* Database types support according to classpath (object and graph db support activated by adding jars to classpath)
* All three database types may be used in single transaction (changes will be visible between different apis)
* Hooks for schema migration and data initialization extensions
* Extension for orient object to scheme mapper with plugins support
* Auto mapping entities in package to db scheme or using classpath scanning to map annotated entities
* Auto db creation (for memory, local and plocal)
* Different db users may be used (for example, for schema initialization or to use orient security model), including support for user change inside transaction
* Support method retry on ONeedRetryException
* Spring-data like repositories with advanced features (e.g. generics usage in query). Great abilities for creating reusable parts (mixins). Support plugins.
* Out of the box crud and pagination mixins provided.

### Setup

Releases are published to [bintray jcenter](https://bintray.com/bintray/jcenter) (package appear immediately after release) 
and then to maven central (require few days after release to be published). 

[![Download](https://api.bintray.com/packages/vyarus/xvik/guice-persist-orient/images/download.svg?ts=2) ](https://bintray.com/vyarus/xvik/guice-persist-orient/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/guice-persist-orient/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/guice-persist-orient)

Maven:

```xml
<dependency>
<groupId>ru.vyarus</groupId>
<artifactId>guice-persist-orient</artifactId>
<version>3.1.0</version>
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
compile ('ru.vyarus:guice-persist-orient:3.1.0'){
    exclude module: 'orientdb-graphdb'
    exclude module: 'orientdb-object'       
}
```

For orient 1.x use version 2.1.0 (see [old docs](https://github.com/xvik/guice-persist-orient/tree/orient-1.x))

By default, only document database support is enabled. 

Remove exclusions to enable object and graph db support.

NOTE: It's very important for object db to use exact `javassist` version it depends on. If other libraries in 
your classpath use `javassist`, check that newer or older version not appear in classpath.

##### Snapshots

You can use snapshot versions through [JitPack](https://jitpack.io):

* Go to [JitPack project page](https://jitpack.io/#xvik/guice-persist-orient)
* Select `Commits` section and click `Get it` on commit you want to use (top one - the most recent)
* Follow displayed instruction: add repository and change dependency (NOTE: due to JitPack convention artifact group will be different)

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

Auto database creation for local types (all except 'remote') is enabled by default, but you can switch it off. 
Auto creation is nice for playing/developing/testing phase, but most likely will not be useful for production.

```java
install(new OrientModule(url, user, password)
                .autoCreateLocalDatabase(false));
```

Default transactions configuration may be specified as additional module parameter.
By default, OPTIMISTIC transactions used (use optimistic locking based on object version, same way as hibernate optimistic locking). 
NOTX mode disables transactions. Read more about [transactions in orient](http://www.orientechnologies.com/docs/last/orientdb.wiki/Transactions.html)

For example, to switch off transactions use:

```java
install(new OrientModule(url, user, password)
                .defaultTransactionConfig(new TxConfig(OTransaction.TXTYPE.NOTX));
```

### Usage

#### Lifecycle

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

#### Unit of work (transaction)

Unit of work defines transaction scope. Actual orient transaction will start only on first connection acquire (so basically, unit of work
may not contain actual orient transaction, but for simplicity both may be considered equal).

Unit of work may be defined by:
* `@Transactional` annotation on guice bean or single method (additional `@TxType` annotation allows to define different transaction type for specific unit of work)
* Inject `PersistentContext` bean into your service and use its methods
* Using `TransactionManager` begin() and end() methods.

First two options are better, because they automatically manage rollbacks and avoid not closed (forgot to call end) transactions.
Read more about [orient transactions](http://www.orientechnologies.com/docs/last/orientdb.wiki/Transactions.html)

**NOTE**: orient 2 is more strict about transactions: now ODocument could be created only inside transaction and object proxy
can't be used outside of transaction (but from 2.0.5 proxies work again).

When you get error:

```
Database instance is not set in current thread. Assure to set it with: ODatabaseRecordThreadLocal.INSTANCE.set(db)
```

It almost certainly means that you perform transactional operation outside of transaction. Simply enlarge transaction scope.

###### Examples

Defining transaction for all methods in bean (both method1 and method2 will be executed in transaction):

```java
@Transactional
public class MyBean {
    public void method1() ...
    public void method2() ...
}
```

Defining no tx transaction on method:

```java
@Transactional
@TxType(OTransaction.TXTYPE.NOTX)
public void method()
```

Notx usually used for scheme updates, but in some cases may be used to speed up execution (but in most cases its better to use transaction for consistency).

Using `PersistentContext`:

```java
@Inject PersistentContext<OObjectDatabaseTx> context;

...
context.doInTransaction(new TxAction<Void>() {
        @Override
        public Void execute() throws Throwable {
            // something
            return null;
        }
    });
```

Using `TransactionManager`:

```java
@Inject TransactionManager manager;
...
manager.begin();
try {
// do something
    manager.end();
} catch (Exception ex) {
    manager.rollback()
}
```

NOTE: in contrast to spring default proxies, in guice when you call bean method inside the same bean, annotation interceptor will still work.
So it's possible to define few units of work withing single bean using annotations:

```java
public void nonTxMethod(){
    doTx1();
    doTx2();
}

// methods can't be private (should be at least package private)
@Transactional
void doTx1() {..}

@Transactional
void doTx2() {..}
```

#### Connections

Document is the core connection type. Object and graph apis use document connection internally.
Connection object mainly defines result of queries: document connection will always return ODocument,
object connection returns mapped pojos (actually proxies) and ODocument for fields selections and
graph api returns Vertex and Edge types.
And of course connections provide specific apis for types.

You can use any connections within single transaction and changes made in one connection type will be visible
in other connections. This allows you, for example to update data using object api and create relations using graph api.

To access connection object inside transaction use `PersistentContext` generified with the type of required connection.

* `PersistentContext<OObjectDatabaseTx>` for object database connection
* `PersistentContext<ODatabaseDocumentTx>` for document database connection
* `PersistentContext<OrientBaseGraph>` for graph database connection (transactional or not)
* `PersistentContext<OrientGraph>` for transactional graph database connection (will fail if notx transaction type)
* `PersistentContext<OrientGraphNoTx>` for non transactional graph database connection (will provide only for notx transaction type, otherwise fail)

Note: you can't use both OrientGraph and OrientGraphNoTx in the same transaction (type must be used according to transaction type).
OrientBaseGraph may be used in places where both types are possible (its the base class for both).

`PersistentContext` methods are shortcuts for low level api (simplifies usage). You can extend it to add more shortcut methods
or make your own: it is not used internally and exists only for public usage.

See full [api description](https://github.com/xvik/guice-persist-orient/wiki/Core-API)

#### Scheme initialization

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

Scheme initializer is called in notx unit of work (orient requires database scheme updates to be performed without transaction).

By default, no-op implementation enabled.


##### Object scheme mapping

Database scheme may be initialized from classes (jpa like).
See [orient object mapping documentation](http://www.orientechnologies.com/docs/last/orientdb.wiki/Object-2-Record-Java-Binding.html) for object mapping
 ([and general object database page](http://www.orientechnologies.com/docs/last/orientdb.wiki/Object-Database.html)).

Default orient object scheme mapper is limited in [some cases](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#default-object-scheme-mapping).
Special scheme mapper implementation provided (extension to default orient mapper).

It provides additional mapping annotations:
* [@EdgeType](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#edgetype) - register class as edge type
* [@VertexType](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#vertextype) - register class as vertex type
* [@RenameFrom](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#renamefrom) - renames existing scheme class before class registration
* [@Recreate](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#recreate) - drop and create fresh scheme on each start
* [@CompositeIndex](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#compositeindex) - creates composite index for class (index span multiple properties)
* [@CompositeLuceneIndex](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#compositeluceneindex) - creates composite lucene index for class (index span multiple 
* [@DropIndexes](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#dropindexes) - drops existing indexes on start
* [@RenamePropertyFrom](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#renamepropertyfrom) - renames existing scheme property before class registration
* [@Index](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#index) - creates index for annotated field
* [@FulltextIndex](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#fulltextindex) - creates fulltext index for annotated field
* [@LuceneIndex](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#luceneindex) - creates lucene index for annotated field
* [@Readonly](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#readonly) - marks property as readonly
* [@ONotNull](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#onotnull) - marks property as not null
* [@Mandatory](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#mandatory) - marks property as mandatory
* [@CaseInsensitive](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#caseinsensitive) - marks property as case insensitive

New annotation could be easily [implemented as extensions](https://github.com/xvik/guice-persist-orient/wiki/Object-scheme-initializer#extensions)
(and all existing annotation are extensions and may be replaced).

Default modules provided with custom initializers (which use extended scheme mapping mechanism):
* `PackageSchemeModule` - register all model classes in specific package (or packages). Useful when all model classes located in one package
* `AutoScanSchemeModule` - registers all model classes annotated with `@Persistent` annotation. Useful for package by feature approach,
when model classes contained within related logic

Module must be registered together with main `OrientModule`, for example:

```java
install(new AutoScanSchemeModule("my.model.root.package"));
```

For example, to create vertex scheme:

```java
@VertexType
public class MyVertex {..}
```

Now MyVertex could be used with graph api and with object api.
Graph relations may be created dynamically (even if they are not mapped in object).

Remember that scheme created from objects maintain same hierarchy as your objects. E.g. if you use provided `VersionedEntity` class as base
class for entities, it will be also registered in scheme (nothing bad, you may not notice it).
But for graphs hierarchies more important: both vertex and edge objects can't extend same class (root class in hierarchy must extend V or E).
So if you use `@VertexType` or `@EdgeType` annotations make sure their hierarchy not intersect.

#### Data initialization

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
You should define unit of work (maybe more than one) yourself (with annotation or manual).

#### Change user

To use different db user for one or more transactions use `UserManager`:

```java
@Inject UserManager userManager;
...
userManager.executeWithUser('user', 'password', new SpecificUserAction<Void>() {
    @Override
    public Void execute() throws Throwable {
        // do work
    }
})
```

`SpecificUserAction` defines scope of user overriding. This will not implicitly start transaction, but simply
binds different user to current thread.

Overriding may be used in scheme initialization to use more powerful user or to use [orient security model](http://www.orientechnologies.com/docs/last/orientdb.wiki/Security.html)
(in this case overriding may be done, for example in servlet filter).

Nested user override is not allowed (to avoid confusion). But you can use user overriding inside transaction.

##### Change tx user

User may be overridden inside transaction:

```java
// context is PersistentContext
context.doWithUser('user', new SpecificUserAction<Void>() {
    @Override
    public Void execute() throws Throwable {
        // do work
    }
})
```

This changes current connection object user. As a result orient security checks will work for overridden user.
Nested user override is not allowed (to avoid confusion).

More about [user override](https://github.com/xvik/guice-persist-orient/wiki/Core-API#user-manager)

#### Retry

Due to orient implementation specifics, you may face [OConcurrentModificationException](http://www.orientechnologies.com/docs/last/orientdb.wiki/Troubleshooting-Java.html#oconcurrentmodificationexception-cannot-update-record-xy-in-storage-z-because-the-version-is-not-the-latest-probably-you-are-updating-an-old-record-or-it-has-been-modified-by-another-user-dbva-yourvb)
When you was trying to save object its expected - optimistic locking (object contains version and if db version is different
then your object considered stale, and you cant save it).

But, for example, even query like this may fail:

```
update Model set name = 'updated'
```

In concurrent environment this query also may cause OConcurrentModificationException.
There is a special base class for such exceptions: ONeedRetryException.
So by design some operations may fail initially, but succeed on repeated execution.

To fix such places you can use `@Retry` annotation. It catches exception and if its ONeedRetryException (or any cause is retry exception)
it will repeat method execution.

```java
@Retry(100)
@Transactional
public void update()
```

Annotation must be defined outside of transaction, because retry exception is casted on commit and it's impossible to
catch it inside transaction. If @Retry annotated method will be executed under transaction, it will fail (catches redundant definition
and avid error because of "expected behaviour".
So be careful using it: be sure not to use annotated methods in transaction.

@Retry may be used with @Transactional on the same method (retry applied before).

In some cases using script instead of query solves concurrent update problem (even without retry):

```
begin
update Model set name='updated'
commit
```

Anyway, always write concurrent tests to be sure.


## Repository

Repository annotations simplify writing dao or repository objects.
Repositories are very close to spring-data repositories and following description will follow this approach.
But repository methods may be used in any way (like dao or as additional methods for beans).

Repositories mainly cover query definitions (removing all boilerplate code).
If you need something like spring-data specifications, you can use [orientqb](https://github.com/raymanrt/orientqb)

Example repository query method:

```java
@Query("select from Model where name=? and nick=?")
List<Model> find(String name, String nick);
```

Repositories implementation is based on extensions (every annotation you'll see is an extension).
Custom extensions supported, so you can change almost everything.

### Setup

To use repository features register repository module in guice context:

```java
install(new RepositoryModule());
```

### Guice abstract types support

Repository methods defined with annotations, so interface and abstract methods are ideal candidates to use them.
Guice doesn't allow using abstract types, but it's possible with [a bit of magic](https://github.com/xvik/guice-ext-annotations).

Abstract types (abstract class or interface containing repository methods) could be registered directly in guice module:

```java
bind(MyInterfaceRepository.class).to(DynamicClassGenerator.generate(MyInterfaceRepository.class)).in(Singleton.class)
```

Or dynamic resolution could be used (guice JIT resolution):

```java
@ProvidedBy(DynamicSingletonProvider.class)
public interface MyRepository
```

When some bean require this dao as dependency, guice will call provider, which will generate proper class for guice.
(dynamic resolution completely replaces classpath scanning: only actually used repositories will be created)
Note, this will automatically make bean singleton, which should be desirable in most cases. If you need custom scope
use `DynamicClassProvider` with `@ScopeAnnotation` annotation (see details in [guice-ext-annotations](https://github.com/xvik/guice-ext-annotations))

Note: Intellij IDEA will warn you that ProvidedBy annotation is incorrectly typed, but it's ok, because provider is too generic.
There is nothing I can do with it and it's the best (the simplest) way I know (without explicit classpath scanning, which is redundant).

IMPORTANT: guice will control instance creation, so guice AOP features will completely work!
`@Transactional` annotation may be used (generally not the best idea to limit transaction to repository method, but in some cases could be suitable).
You can think of repository interface or abstract class as of usual guice bean (no limitations).

Repository methods are applied using aop (that's why they could be used everywhere).

### Repositories overview

Method annotations:
* [@Query](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#query)  - select/update/insert query
* [@Function](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#function) - function call
* [@Script](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#script) - script call (sql, js etc)
* [@AsyncQuery](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#asyncquery) - async query call
* [@Delegate](https://github.com/xvik/guice-persist-orient/wiki/Repository-delegate-methods#delegate-method) - delegate call to other bean method

All except delegate are command methods (build around orient command objects).

Command methods parameters annotations:
* [@Param](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#param) - named parameter
* [@ElVar](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#elvar) - query variable value (substituted in string before query execution)
* [@RidElVar](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#ridelvar) - extract rid from provided object, document, vertex, string orid and insert into query as el var
* [@Var](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#var) - orient command variable ($var), may be used by query during execution
* [@Skip and @Limit](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#skip-and-limit) - orient pagination
* [@FetchPlan](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#fetchplan) - defines fetch plan for query
* [@Listen](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#listen) - to provide query listener (required for async queries)
* [@DynamicParams](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#dynamicparams) - map dynamic count of parameters from array/collection/map

Delegate method parameters annotations:
* [@Generic](https://github.com/xvik/guice-persist-orient/wiki/Repository-delegate-methods#generic) - generic type value of caller repository (now exact class could be specified where to search generic)
* [@Repository](https://github.com/xvik/guice-persist-orient/wiki/Repository-delegate-methods#reository) - caller repository instance
* [@Connection](https://github.com/xvik/guice-persist-orient/wiki/Repository-delegate-methods#connection) - db connection object, selected by repository method

Command methods amend annotations:
* [@Timeout](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#timeout) - defines query timeout and timeout strategy

Result converter annotations:
* [@NoConversion](https://github.com/xvik/guice-persist-orient/wiki/Repository-result-handling#result-extensions) - disable default result conversion logic
* [@DetachResult](https://github.com/xvik/guice-persist-orient/wiki/Repository-result-handling#detaching-results) - detaches result objects (list or simple object): returned result will contain simple objects instead of proxies

#### Defining repository

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository {

    @Query("select from Model")
    List<Model> selectAll();

    @Query("update Model set name = ? where name = ?")
    int updateName(String newName, String oldName);

    @Query("insert into Model (name) values(:name)")
    Model create(@Param("name") String name);
}
```

Note: also, repository methods could be used to supplement existing bean, but suggest to use pure interface repositories.

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class MyDao {

    @Query("select from Model")
    public abstract List<Model> selectAll();

    // normal method
    public void doSomething() {
        ...
    }
}
```

Note: @Transactional is not required (annotation usage depends on your service architecture, but repository method
must be used inside transaction).


#### Usage examples

[Function](http://www.orientechnologies.com/docs/last/orientdb.wiki/Functions.html) call:

```java
@Function("function1")
List<Model> function();
```

Positional parameters:

```java
@Query("select from Model where name=? and nick=?")
List<Model> parametersPositional(String name, String nick)
```

Named parameters:

```java
@Query( "select from Model where name=:name and nick=:nick")
List<Model> parametersNamed(@Param("name") String name, @Param("nick") String nick)
```

[Pagination](http://www.orientechnologies.com/docs/last/orientdb.wiki/Pagination.html):

```java
@Query("select from Model where name=? and nick=?")
List<Model> parametersPaged(String name, String nick, @Skip int skip, @Limit int limit)
```

[El variable](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods#el-variables):

```java
@Query("select from Model where ${prop}=?")
List<Model> findBy(@ElVar("prop") String prop, String value)
```

[Fetch plan](http://www.orientechnologies.com/docs/last/orientdb.wiki/Fetching-Strategies.html) parameter:

```java
@Query("select from Model")
List<Model> selectAll(@FetchPlan("*:0") String plan);
```

[Sql](http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-batch.html) script:

```java
@Script("begin" +
  "let account = create vertex Account set name = :name" +
  "let city = select from City where name = :city" +
  "let edge = create edge Lives from $account to $city" +
  "commit retry 100" +
  "return $edge")
Edge linkCity(@Param("name") String name, @Param("city") String city)
```

[Js](http://www.orientechnologies.com/docs/last/orientdb.wiki/Javascript-Command.html) script:

```java
@Script(language = "javascript", value =
 "for( i = 0; i < 1000; i++ ){" +
     "db.command('insert into Model(name) values (\"test'+i+'\")');" +
 "}")
void jsScript()
```

[Async](http://www.orientechnologies.com/docs/last/orientdb.wiki/Document-Database.html#asynchronous-query) query:

```java
@AsyncQuery("select from Model")
void select(@Listen OCommandResultListener listener)
```

Dynamic parameters:

```java
@Query('select from Model where ${cond}')
List<ODocument> findWhere(@ElVar("cond") String cond, @DynamicParams Object... params);
```

[Delegate](https://github.com/xvik/guice-persist-orient/wiki/Repository-delegate-methods) example:

```java
public class SomeBean {
   public List getAll() {
      ...
   }
}

@Delegate(SomeBean.class)
List getAll();
```

Read more about method usage in wiki:
* [Command methods](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods)
* [Delegate methods](https://github.com/xvik/guice-persist-orient/wiki/Repository-delegate-methods)

Writing extensions:
* [Extending commands](https://github.com/xvik/guice-persist-orient/wiki/Repository-command-methods-internals)
* [Extending delegates](https://github.com/xvik/guice-persist-orient/wiki/Repository-delegate-methods-internals)

#### Return types

You can use Iterable, Collection, List, Set, any collection implementation, array, single element or Iterator as return type.
Conversion between types will be applied automatically.

```java
@Query("select from Model")
List<Model> selectAll();

@Query("select from Model")
Set<Model> selectAll();

@Query("select from Model")
Model[] selectAll();

@Query("select from Model")
Iterable<Model> selectAll();

@Query("select from Model")
Iterator<Model> selectAll();
```

If you define single result, when query produce multiple results, first result would be automatically taken:

```java
@Query("select from Model limit 1")
Model selectAll();
```

Note: limit is not required, but preferred, as soon as you don't need other results

##### Projection

In some cases single value is preferred, for example:

```java
@Query("select count(@rid) from Model)
int count();
```

Orient returns ODocument from query with single field (count).
Default result converter could recognize when document or vertex contain just one property and return only simple value.

Another case is when you select single field:

```java
@Query("select name from Model")
String[] selectNames()
```

Read more about [projection](https://github.com/xvik/guice-persist-orient/wiki/Repository-result-handling#result-projection)

##### Result type definition

It is very important to always define exact return type. Connection type defines type of result object:
document connection always return ODocument, object return mapped objects (but ODocument for field calls)
and graph - Vertex and Edge.

Result type is used internally to detect connection type for query.

For example, if you write:

```java
@Query("select from Model")
List selectAll();
```

You will actually receive `List<ODocument>`, because without generic it's impossible to detect required return type
and document connection used for query.

For example, in this case graph connection would be selected:

```java
@Query("select from Model")
List<Vertex> selectAll();
```

##### Result conversion

Every repository method result is converted with default converter (as described above).

You can use more specific result conversion extension, for example:

```java
@Query("select from Model")
@NoConversion
List<Model> selectAll();
```

NoConversion disables conversion mechanism and you receive result object as is.

With object connection, orient always return proxy objects, which usage outside of transaction is limited
(same as in jpa). You can use detach converter to receive pure pojos:

```java
@Query("select from Model")
@DetachResult
List<Model> selectAll();
```

Read more about [converter mechanism and writing custom converters](https://github.com/xvik/guice-persist-orient/wiki/Repository-result-handling).

#### Mixins

Java support multiple inheritance for interfaces and you can inherit multiple interfaces in classes.
So interfaces are ideal for writing small reusable parts (mixins).

##### Command mixins

El variables in commands support references to class generics, so you can use it for generic repository logic:

```java
public interface MyMixin<T> {

    @Query("select from ${T}")
    List<T> selectAll()
}

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends MyMixin<Model> {}
```

When you call mixin method from repository instance

```java
repository.selectAll()
```

Generic value Model will be used for command `select from Model` and return type will be resolved as `List<Model>`,
which will allow to select proper connection (object if Model is mapped entity).

You may use as many generics as you need. Any repository hierarchy depth will be correctly resolved, so
you can even use composition mixins, which wil simply combine commonly used mixins:

```java
public interface RepositoryBase<T> extends Mixin1<T>, Mixin2<T> {}
```

Note that you don't need to set ProvidedBy annotation on mixins, because it's just interfaces and they are not used as repository instances.

##### Delegate mixins

Delegates are also support generalization through extensions:

```java
public class DelegateBean {
    public List selectAll(@Generic("T") Class model) {

    }
}

public interface MyMixin<T> {
    @Delegate(DelegateBean.class)
    List<T> selectAll()
}
```

When delegate bean called from mixin, it will receive generic value (of calling mixin) as parameter.

Read more about [mixins usage](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins)

##### Bundled mixins

Few mixins provided out of the box:

* [DocumentCrud](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#documentcrud)
* [ObjectCrud](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#objectcrud)
* [ObjectVertexCrud](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#objectvertexcrud)
* [EdgesSupport](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#edgessupport)
* [EdgeTypeSupport](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#edgetypesupport)
* [Pagination](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#pagination)

Crud mixins are the most common thing: commonly these methods are implemented in `AbstractDao` or something like this.

[DocumentCrud](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#documentcrud) mixin provides base crud methods for document repository.

```java
public interface MyEntityDao extends DocumentCrud<MyEntity> {}
```

Set mixin generic value only if you have reference entity class. Generic affects only `getAll` and `create` methods: if generic not set
you will not be able to use only this method.

[ObjectCrud](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#objectcrud) mixin provides base crud methods for object repository:

```java
public interface MyEntityRepository extends ObjectCrud<MyEntity> {}
```

Now MyEntityRepository has all basic crud methods (create, get, delete etc).

[Pagination](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#pagination) provides simple pagination for your entity or document (but document should have reference type,
at least to specify schema type name (may be empty class))

```java
public interface MyEntityRepository extends ObjectCrud<MyEntity>, Pagination<MyEntity, MyEntity> {}

...
// return page
Page page = repository.getPage(1, 20);
```

In order to use pagination mixin, crud mixin is not required (used in example just to mention one more time that mixins could be combined).
Pagination mixin is the most complex one and good place to inspire how to [write more complex reusable logic](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#implementation).

[ObjectVertexCrud](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#objectvertexcrud), 
[EdgesSupport](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#edgessupport) and
[EdgeTypeSupport](https://github.com/xvik/guice-persist-orient/wiki/Repository-mixins#edgetypesupport) mixins allows using graph features from object api.

```java
@vertexType
public class Model {}

@EdgeType
public class ModelConnection {}

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends ObjectVertexCrud<Model>, 
                       EdgeTypeSupport<ModelConnection, Model, Model> {}
                       
@Inject ModelRepository repository;
...
Model from = repository.save(new Model(..));
Model to = repository.save(new Model(..));
ModelConnection edge = repository.createEdge(from, to);
```

#### Validation

You can use [guice-validator](https://github.com/xvik/guice-validator) to apply runtime validation (jsr 303) for repository methods:

```java
@Query("select from Model where name = ?")
@Size(min = 1)
List<Model> select(@NotNull String name)
```

Now this query throw ConstraintViolationException if null provided as parameter or no results returned.

Important: register validator module before guice-persist-orient modules!
This way validation will be checked before @Transactional or repository methods logic.

### Advanced topics

* [How repositories work](https://github.com/xvik/guice-persist-orient/wiki/Repository-internals)
* [Connection pools definition](https://github.com/xvik/guice-persist-orient/wiki/Pools)
* [Internal caches](https://github.com/xvik/guice-persist-orient/wiki/Cache)

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

* [generics-resolver](https://github.com/xvik/generics-resolver) - extracted library, used for generics resolution during finders analysis
* [dropwizard-orient-server](https://github.com/xvik/dropwizard-orient-server) - embedded orientdb server for dropwizard
* [guice-validator](https://github.com/xvik/guice-validator) - hibernate validator integration for guice 
(objects validation, method arguments and return type runtime validation)
* [guice-ext-annotations](https://github.com/xvik/guice-ext-annotations) - @Log, @PostConstruct, @PreDestroy and
utilities for adding new annotations support

### Contribution

Contributions are always welcome, but please check before patch submission:

```bash
$ gradlew check
```

-
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
