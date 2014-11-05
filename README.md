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
* Auto db creation (for memory, local and plocal)
* Hooks for schema migration and data initialization extensions
* All three database types may be used in single unit of work (but each type will use its own transaction)
* Different db users may be used (for example, for schema initialization or to use orient security model)
* Finders concept extended from guice-persist with support of method delegates, hierarchies and complete generics recognition. 
This allows writing generic parts (mixins) and re-use them in many finders (thanks to multiple inheritance in interfaces).
Out of the box crud and pagination mixins provided.

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
<version>2.0.0</version>
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
compile ('ru.vyarus:guice-persist-orient:2.0.0'){
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

##### Different users

To use different db user for one or more transactions, get `UserManager` bean from context:

```java
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

Limitations:
* User can't be overridden during transaction (override it before transaction start)
* Overridden user can't be changed (inside overriding scope other overrides are not allowed)

### Dynamic finders

In guice-persist finders allows just to define queries on interface methods. This concept was evolved,
allowing to completely write dao layer using interfaces.

There are two types of finder methods:
* Sql finders, which allows to define sql queries (select/insert/update) on interface methods
* Delegate finders, delegates call of interface method into guice bean method

Finder annotations may be used on interface methods or bean methods.

If finder used in interface, all interface methods must be finder methods and interface must be manually registered in `FinderModule`.
If `AutoScanFinderModule` used, finder interfaces will be registered automatically. 

`@Transactonal` annotation is supported within interface finders (generally not the best idea to limit transaction to finder method, but in some cases could be suitable)

The most powerful thing is finder mixins: interfaces in java support multiple inheritance, and so allows better reuse of generic parts.
More on mixins read below.

#### Sql finders

Example interface method:

```java
@Finder(query = "select from Model where name=? and nick=?")
List<Model> parametersPositional(String name, String nick);
```

Used with bean (e.g. to supplement usual dao methods):

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

It may be important for proper return type: different connections may return different objects (ODocument for document, model instances for object and
Vertex and Edge for graph). For update queries connection type can't be detected and it always executed with default connection 
(document, but may be changed in module configuration), but it may be important to execute it in the same transaction with other changes 
(e.g. if you work in object connection and want update in the same connection).

##### Placeholders

Custom placeholders may be used in queries:

```java
@Finder(query = "select from User where ${provider} = ?")
Optional<User> findByProvider(@Placeholder("provider") AuthProvider provider, String providerId);
```

Placeholders are useful mostly when we have a bunch of almost similar queries. In example above we have user object
with oauth tokens stored for various providers. Without placeholder, we would need to write separate query for each
provider, but with placeholder it could be written as single finder call with enum as additional parameter (very natural).

Placeholder parameters may be strings or enums. Of course, enum is preferred, as most strict contract, but in some 
cases strings may be more helpful. Enum converted to value using enum.toString(), so override it to provide correct 
placeholder value (most likely different from enum.name()).

By using raw string we introduce great opportunity for sql injections (placeholder may insert not just field name, 
but entire subquery). In order to secure string placeholders, use `@PlaceholderValues` annotation to define possible 
placeholder values (and `@Placeholders` to define multiple placeholders):

```java
@Finder(query = "select from Model where ${field} = ?")
@PlaceholderValues(name = "field", values = {"name", "nick"})
Model findByField(@Placeholder("field") String field, String value);


@Finder( query = "select from Model where ${field1} = ? and ${field2} = ?")
@Placeholders({
      @PlaceholderValues(name="field1", values = {"name", "nick"}),
      @PlaceholderValues(name="field2", values = {"name", "nick"})
})
Model findByTwoFields(@Placeholder("field1") String field1, @Placeholder("field2") String field2,
                      String value1, String value2);
```

This way, any other value passed to placeholder parameter will be rejected (string usage become secure as enum).
You can use string placeholders without strict definition, but in this case make sure security will not be violated.

##### Generics (generified mixins)

Finder interfaces could extend other interfaces - entire finder interface hierarchy is parsed and all generics properly
resolved. For example:

```java
public interface Base<T> {
    @Finder(query = "..")
    List<T> getSomething();
}

public interface ActualFinder extends Base<Model> {}
```

Is absolutely valid finder. Return type will be properly resolved with real generic value and all detections/conversions
could apply. The only problem is query.

To solve this problem, queries support generic placeholders:

```java
@Finder(query = "select from ${T}")
List<T> getSomething();
```

No matter how complicated generic is, it will still be resolved, e.g.:

```java
public interface Base<T, K extends List<T>> {
    @Finder(query = "select from ${T}")
    K getSomething();
}
```

Generics correctly resolved even for multiple hierarchy levels.

Generified base interfaces are the simplest type of mixins (generic reusable parts).

#### Delegate finder

Delegates execution to guice bean method, for example interface method:

```java
@FinderDelegate(TargetBean.class)
List<Model> selectSomething();
```

On execution, delegating method will be found in TargetBean and executed. 
This allows writing queries using java api, but still use interface finder to call it. So finder become single point
for your entity's dao methods, whereas actual implementation could be decomposed by multiple beans.
 
Delegation will work on beans too:
 
```java
@FinderDelegate(TargetBean.class)
public List<Model> selectSomething() {
  throw new UnsupportedOperationException("Should be handled with finder interceptor");
}
```

##### Method lookup algorithm

`@FinderDelegate` annotation allows you to define
* target implementation type with `value` attribute
* exact target method name with `method` attribute (this must be used as last resort, because it introduce weak contract and not refactor-friendly)
* override result collection implementation with `returnAs` attribute (the same as with `@Finder` annotation)

Method is searched only as direct public method of target bean, ignoring it's hierarchy. 
This may change in future, but currently this reduces scope for searching.

Algorithm:
* If method name set directly (annotation method attribute), look only methods with this name. If method name not set look all public methods.
* Check all methods for parameter compatibility. Target method must have compatible parameters
at the same order(!) Special parameters (see below) may appear at any position (before/after/between).
* If more then one method found, use finder method name to reduce results (this should be the most useful hint)
* Method with special parameters is prioritized. So if few methods found but only one has additional parameters - it will be chosen.
* Next methods are filtered by most specific parameters (e.g. two methods with the same name but one declares
String parameter and other Object; first one will be chosen as more specific).
* If we still have more than one possibility, error will be thrown.

##### Delegate method implementation specifics

Delegate bean may be not aware that it is delegate (maybe already existent bean). In this case method parameters must be 1 to 1
compatible to finder parameters.

If delegated method written specifically for finder, it may use extended annotated parameters:
* `@FinderGeneric` pass resolved finder interface generic type as parameter
* `@FinderInstance` pass finder itself as parameter; parameter type may be any type finder is assignable to
* `@FinderDb` pass resolved connection type object as parameter (see connection type detection below); parameter type should be assignable with connection 
(e.g. for document and object `ODatabaseComplex` may be used as common abstraction)

For example, suppose we have generic finder mixin:

```java
public interface MyBase<T> {

    @FinderDelegate(TargetBean.class)
    List<T> doSomething1(int a)
    
    @FinderDelegate(TargetBean.class)    
    List<T> doSomething2(int b)
    
    @FinderDelegate(TargetBean.class)    
    List<T> doSomething3(int c, int d)
}

public class TargetBean {

    List doSomething1(@FinderGeneric("T") Class<?> type, int a) {...}
    
    List doSomething2(int b, @FinderInstance MyBase finder) {...}
    
    List doSomething3(int c, @FinderDb ODatabaseComplex db, int b) {...}
}
```

All three methods would be properly resolved (thanks to method names). Also this shows that position of extended 
parameter is not important (only order of usual parameters is important). And of course, more than one special parameter
may be used for single method.

Now suppose we have root finder implementing mixin:

```java
public Finder extends MyBase<Model> {}
```

If we call `doSomething1`, `Model` will be passed as first parameter.
For `doSomething2` `Finder` will be passed (proxy around interface). And for `doSomething3` `OObjectDatabaseTx` will be passed
as db connection instance (suppose that Model is registered entity type).

Connection parameter may be used to simply avoid using provider for obtaining connection (suppose
you're writing delegated graph query):

```java
public List<Vertex> doSearch(@FinderDb OrientGraph db, Integer someParam) {
    db.//do something using connection object directly
}
```

##### Delegate mixins

In root finder you must use `@FinderDelegate` annotation on methods, but for mixins, you may place annotation
on type (example above could be re-written):

```java
@FinderDelegate(TargetBean.class)
public interface MyBase<T> {
    ...
}    
```    

All methods will search for delegating method in `TargetBean`.

The best possible way for writing delegate mixins is to implement finder interface by implementing bean: 
this makes the most strongest contract between finder mixin interface and implementation (also, IDE will provide direct
reference for implementation from interface).

In case, when you need extended method parameters: implement direct method as throwing exception and write extended method
below it, e.g:

```java
@Override
public T create() {
    // finder should choose extended method instead of direct implementation
    throw new UnsupportedOperationException("Method create(Class) must be called");
}

public T create(@FinderGeneric("T") final Class<T> type) {
    return dbProvider.get().newInstance(type);
}
```

Writing delegation mixins is a bit hard, but resulted mixins are very easy (obvious) to use.

##### Delegate beans

Delegate beans are obtained from guice context, so you can use any scopes for beans. But most of the time singleton
will perfectly fits your needs.

It is not required to explicitly register delegate beans - guice will be able to create instance in runtime even if bean
was not registered.

##### Bundled mixins

Few mixins provided out of the box. 

Crud mixins are the most common thing: most likely these methods are implemented in your `AbstractDao` or something like this.

`ObjectCrudMixin` provides base crud methods for object finder:

```java
public interface MyEntityDao extends ObjectCrudMixin<MyEntity> {}
```

Now MyEntityDao has all basic crud methods (create, get, delete etc).

`DocumentCrudMixin` provides base crud methods for document dao.

```java
public interface MyEntityDao extends DocumentCrudMixin<MyEntity> {}
```

Set mixin generic value only if you have reference entity class. Generic affects only `getAll` method: if generic not set
you will not be able to use only this method.

`PaginationMixin` provides simple pagination for your entity or document (but document should have reference type, 
at least to specifyschema type name (may be empty class))

```java
public interface MyEntityDao extends ObjectCrudMixin<MyEntity>, PaginationMixin<MyEntity, MyEntity> {}

...
// return page
Page page = myFinderDao.getPage(1, 20);
```

In order to use pagination mixin, crud mixin is not required (used in example just to show how mixins could be combined).
Pagination mixin is the most complex one and good place to inspire how to write non trivial mixins.

#### Return types

You can use: `Iterable`, `Collection`, `List`, `Set`, any collection implementation, array, single element or `Iterator`.
Single elements (single object return) may be wrapped with `Optional` (guava (com.google.common.base.Optional) 
or jdk8 (java.util.Optional)). Collections should not be wrapped
with optional, because finder will never return null for collection or array. 
Query execution result will be converted in accordance with specified return type.

```java
@Finder(query = "select from Model")
Model selectAll()
```

```java
@Finder(query = "select from Model")
Optional<Model> selectAll()
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

###### Result projection

In orient, when you query for some aggregated function (like count) or selecting just one field, ODocument
or Vertex objects will be returned (for document/object and graph connections). This is usually not the desired behaviour.

Projection is unwrapping from document or vertex if it contains just one property. Unwrapping is triggered by return type, e.g.

```java
@Finder(query = "select count(@rid) from Model")
int getCount();
```

Here return type is int, but actual query will return ODocument. Result converter will detect this, look that document contains
just one field (count) and return just this field value. 
Note that actual field value could be long or double, conversion to int will also be performed automatically.
If return type would be ODocument - no conversion will occur.

When we need just one field from multiple rows:

```java
@Finder(query = "select name from Model")
String[] getNamesArray();
```

Query returns collection of ODocument, but result converter will look return type and unwrap documents returning you simple array.

Automatic projection will work ONLY with array: if you try to set return type as List<String> you will actually get list 
of documents (due to type erasure this would be valid in runtime). Detection not implemented for collections because
at least one element is required to check, but orient tends to return iterators, which mean entire iterator must be repackaged to 
a new one just for one check (probably redundant). To reduce this overhead, projection is triggered only by arrays (easy to check - no overhead).

For graph connection this will also work:

```java
@Finder(query = "select name from Model")
@Use(DbType.GRAPH)
String[] getNamesArray();
```

This time orient will return Vertex instances and result converter will look if vertex contains just one property and if so 
will unwrap single value.

Special case: by default, result converter took first collection element if single result required. So projection may be used like this:

```java
@Finder(query = "select name from Model")
String getNamesArray();
```

Here collection reduced to one element and single element projected to single value.

#### Connection type detection

Connection type can be detected for queries using method return type. 
If you specify generic for returned collection (or iterator) or use typed array (not Object[]) or in case of single element,
connection type will be detected like this:
* If returned class is registered with object entity manager, object connection will be used
* If `ODocument` class returned - document connection used 
* If `Vertex` or `Edge` classes returned - graph connection

But, for example, even if you try to select not object itself, but fields you will get ODocument, even in object connection
(kind of result set in jdbc). For such case document connection will be selected (according to return type), 
but it may be part of object connection logic (e.g. all other queries in object connection). In this case `@Use` annotation will help.

#### Performance

Finders involve a lot of reflection (especially delegates), but it doesn't mean they are slow. 
Finder method is checked on first invocation (so when app starts it doesn't mean everything is ok).
On first finder invocation method descriptor object is composed and cached. On next call everything proxy will have to do
is to prepare special parameters (placeholders, custom delegate params etc) and perform result conversion (which may not occur).

There is very simple benchmark in tests `FinderBenchmarkTest`, you can play with it and see real overhead (yes it's groovy so numbers 
are not accurate, but you can see the big picture).

```
Direct method call: 352,5 μs
Reflection method call: 352,1 μs
Sql finder call: 353,0 μs
Delegate finder method call: 353,4 μs
```

Again, don't believe this numbers, it's just to show there is no large overhead with finders.

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

### Contribution

Contributions are always welcome, but please check before patch submission:

```bash
$ gradlew check
```

-
[![Slush java lib generator](http://img.shields.io/badge/Powered%20by-Slush%20java%20lib%20generator-orange.svg?style=flat-square)](https://github.com/xvik/slush-lib-java)
