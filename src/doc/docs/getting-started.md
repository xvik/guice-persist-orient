# Getting started

## Installation

Maven:

```xml
<dependency>
    <groupId>ru.vyarus</groupId>
    <artifactId>guice-persist-orient</artifactId>
    <version>{{ gradle.version }}</version>
</dependency>
<!--
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-object</artifactId>
    <version>3.1.12</version>
</dependency>
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-graphdb</artifactId>
    <version>3.1.12</version>
</dependency>-->
```

Gradle:

```groovy
implementation 'ru.vyarus:guice-persist-orient:{{ gradle.version }}'
//implementation "com.orientechnologies:orientdb-object:3.1.12"
//implementation "com.orientechnologies:orientdb-graphdb:3.1.12"
```

!!! tip
    Add object and graph dependencies if support required.
    

!!! important
    It's very important for object db to use exact `javassist` version it depends on. If other libraries in 
    your classpath use `javassist`, check that newer or older version not appear in classpath.

## Usage

### Install guice module

```java
install(new OrientModule(url, user, password));
```    

See [orient documentation](https://orientdb.org/docs/3.1.x/datamodeling/Concepts.html#database-url) for supported db types.
In short:

* `memory:dbname` to use in-memory database
* `embedded:dbname` or `plocal:` to use embedded database (no server required, local fs folder will be used); db name must be local fs path
* `remote:dbname` to use remote db (you need to start server to use it)

By default, use `admin/admin` user.

!!! note
    Auto database creation for local types is enabled by default, but you 
    [can switch it off](guide/configuration.md#auto-database-creation).  

    Remote db creation might be enabled manually with `OrientModule#autoCreateRemoteDatabase(user, pass, type)

To change default orient configuration use:

```java
install(new OrientModule(url, user, password).withConfig(config));
```

Where `config` is `OrientDBConfig`. By default, `OrientDBConfig.defaultConfig()` is used.
    
### Lifecycle

You need to manually start/stop persist service in your code (because only you can control application lifecycle).
On start connection pools will be initialized, database created (if required) and scheme/data initializers called. Stop will shutdown
all connection pools.

```java
@Singleton
public class MyLifecycle {
    
    @Inject
    private PersistService orientService;
    
    public void start(){
        orientService.start();
    }
    
    public void stop(){
        orientService.stop();
    }
}
```

Assuming start and stop methods are called on application startup/shutdown. 

### Connections

[Document](https://orientdb.org/docs/3.1.x/java/Document-Database.html) (actually [multi-model](https://orientdb.org/docs/3.1.x/java/Java-MultiModel-API.html)) 
is the core connection type. [Object](https://orientdb.org/docs/3.1.x/java/Object-Database.html) and [graph](https://orientdb.org/docs/3.1.x/java/Graph-Database-Tinkerpop.html) 
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
    you can't use both OrientGraph and OrientGraphNoTx in the same transaction (type must be used according to transaction type).
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

### Transactions

There are 3 ways to declare [transaction](https://orientdb.org/docs/3.1.x/internals/Transactions.html):

* `@Transactional` annotation on guice bean or single method (additional `@TxType` annotation allows to define different transaction type for specific unit of work)
* Inject `PersistentContext` bean into your service and use its methods
* Using `TransactionManager` begin() and end() methods (low level trasaction control).

First two options are better, because they automatically manage rollbacks and avoid not closed (forgot to call end) transactions.

For example, to declare transaction using annotation:

```java
@Transactional
public class MyService {
    public void doSomething() {...}
}
```

Or

```java
public class MyService {
    @Transactional
    public void doSomething() {...}
}
```

!!! warning
    Private methods can't be annotated with `@Transactional`: method must be at least package-private.

Transaction scopes could intersect: e.g. if transactional method calls bean, also annotated as transactional,
then only root transaction scope will work (no sub-transaction or anything like this).  

!!! tip
    If you need to execute without transaction, you still need to declare it
    ```java
    @Transactional
    @TxType(OTransaction.TXTYPE.NOTX)
    public void method()
    ```
    This is required, for example, for schema updates

!!! note
    in contrast to spring default proxies, in guice when you call bean method inside the same bean, annotation interceptor will still work.
    So it's possible to define few units of work withing single bean using annotations:

    ```java
    public void nonTxMethod(){
        doTx1();
        doTx2();
    }
    
    @Transactional
    void doTx1() {..}
    
    @Transactional
    void doTx2() {..}
    ```

!!! warning
    When you get error like    
    ```
    Database instance is not set in current thread. Assure to set it with: ODatabaseRecordThreadLocal.instance().set(db)
    ```    
    It almost certainly means that you perform transactional operation outside of transaction. Simply enlarge transaction scope.    
    
    When you inside transaction, `activateOnCurrentThread()` is called each time you obtain connection from raw connection provider or PersistentContext
    and you will always have correctly bound connection.    
    
    For example, even document creation (`new ODocument()`) must be performed inside transaction.

### Schema initialization

In order to work with database, you obviously need to initialize it's schema first.

```java
public class MySchemeInitializer implements SchemeInitializer {

    @Inject
    private Provider<ODatabaseObject> provider;
    
    public void initialize() {
        final  ODatabaseObject db = provider.get();
        if (db.getMetadata().getSchema().existsClass("Account")) {
            // schema already created - do nothing
            return;            
        }
        
        OClass account = db.getMetadata().getSchema().createClass("Account");
        account.createProperty("id", OType.Integer);
        account.createProperty("birthDate", OType.Date);
        
        //or using sql statements
        db.command(new OCommandSQL("CREATE CLASS Account")).execute();         
    }
}
```


To do it register implementation of `ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer` 
in guice module:

```java
bind(SchemeInitializer.class).to(MySchemeInitializer.class);
``` 

Initializer is called without transaction (NOTX transaction type) as orient requires
database updates to be performed without transaction.

#### Object scheme mapping

If you are using object api, then you can create schema directly from your model objects.
In this case you dont need to implement your own `SchemeInitializer`, instead register module:

```java
install(new AutoScanSchemeModule("my.model.root.package"));
```

Module will find all classes, annotated with `@Persistent` using classpath scan and register them in orient.

For example:

```java
@Persistent
public class Account {
        @Id
        private String id;
        @Version
        private Long version;
        private String myProp;
        
        // getters and setters        
}
```

!!! tip
    If you want to use graph api with objects then annotate your class as `@VertexType` or `@EdgeType`
    in order to correct create graph schema from object.

More about [object scheme mapping](mapping/objectscheme.md).
    
### Data initialization

You may need to initialize some sample data before start (e.g.for testing) or perform some
data updates. Implement `ru.vyarus.guice.persist.orient.db.data.DataInitializer`:

```java
public class MyDataInitializer implements DataInitializer {
    @Inject
    private Provider<ODatabaseObject> provider;
    
    @Override
    @Transactional
    void initializeData() {
        final  ODatabaseObject db = provider.get();
        // init only if database is empty
        if (db.getMetadata().getSchema().getClass(Account.class).count() == 0) {
            db.save(new Account("something 1"));
            db.save(new Account("something 2"));
        }
    }
}
```       

### Repositories 

You can also use interfaces for queries declaration (very like spring-data).

Register repositories module:

```java
install(new RepositoryModule());
```

Now you can declare:

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface AccountRepository {
    
    @Query("select from Account")
    List<Account> selectAll(); 
    
    @Query("update Account set name = ? where name = ?")
    int updateName(String newName, String oldName);
    
    @Query("insert into Account (name) values(:name)")
    Account create(@Param("name") String name);
}
```

Later this repository could be injected as normal guice bean:

```java
@Transactonal
public class MyService {
    @Inject
    private AccountRepository repo;
    
    public void doSomething() {
        List<Account> all = repo.selectAll();
    }
}
```

Or you can use repository methods directly in other beans:

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class MyDao {

    @Query("select from Account")
    public abstract List<Account> selectAll();

    // normal method
    public void doSomething() {
        List<Account> all = selectAll();
        ...
    }
}
```


!!! tip
    IDE could warn you about `@ProvidedBy(DynamicSingletonProvider.class)`, but it's the only way to avoid additional
    (direct) registrations. If warning is annoying for you, you can register beans manually instead:
    ```java
    bind(AccountRepository.class).to(DynamicClassGenerator.generate(MyInterfaceRepository.class)).in(Singleton.class)
    ```    
    
!!! note
    Guice aop will work on repositories (in fact it's implemented using aop), so you may
    use any other aop-driven annotations there.    

More about [repositories](repository/overview.md)    

### Summary

If you want to use orient with object mappings, then guice initialization will look like:

```java
public class MyModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new OrientModule("memory:test", "admin", "admin"));
        install(new AutoScanSchemeModule("my.model.root.package"));
        install(new RepositoryModule());
    }
}
```    

And you will need to start and shutdown persistence support:

```java

@Inject
private PersistService orientService;

// called manually
orientService.start();

...

// called manually before shutdown
orientService.stop();
```