# Configuration

Core orient integration is provided by `OrientModule`:

```java
install(new OrientModule(url, user, password));
```

See [orient documentation](https://orientdb.org/docs/3.1.x/datamodeling/Concepts.html#database-url) for supported db types.
In short:

* `memory:dbname` to use in-memory database
* `embedded:dbname` or `plocal:` to use embedded database (no server required, local fs folder will be used); db name must be local fs path
* `remote:dbname` to use remote db (you need to start server to use it)

By default use `admin/admin` user.

## Auto database creation

Auto database creation for local types (all except 'remote') is enabled by default, but you can switch it off. 
Auto creation is nice for playing/developing/testing phase, but most likely will not be useful for production.

```java
install(new OrientModule(url, user, password)
                .autoCreateLocalDatabase(false));
```

Remote database creation could be enabled with:

```java
install(new OrientModule(url, user, password)
        .autoCreateRemoteDatabase(serverUser, pass, dbType));
```

### Remote auto creation in tests

There is a special shortcut for enabling remote database creation in tests:

```java
OrientDBFactory.enableAutoCreationRemoteDatabase(serverUser, serverPassword, dbType)
```

Must be called before persistence service startup.
The behaviour is the same as with direct module configuration

Static call just sets system properties, used internally for implicit configuration.,

To clean up such properties (if required) use:

```java
OrientDBFactory.disableAutoCreationRemoteDatabase()
```

## Orient configuration

In order to modify new `OrientDBConfig` values use:

```java
OrientDBConfig config = OrientDBConfig.builder()...build()
install(new OrientModule(url, user, password)
                .withConfig(config));
```

By default, config is `OrientDBConfig.defaultConfig()`

### Runtime configuration changes

At runtime configuration could be done changed on instance:

```java
@Inject Provider<ODatabaseDocument> db;

db.get().getStorage().getConfiguration()
```

Or globally (even before startup):

```java
OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
```

Read about [all configuration options](https://orientdb.org/docs/3.1.x/admin/Configuration.html)


## Default transaction type

Default [transactions](https://orientdb.org/docs/3.1.x/internals/Transactions.html) configuration may be specified as additional module parameter.
By default, [OPTIMISTIC transactions](https://orientdb.org/docs/3.1.x/internals/Transactions.html#optimistic-transaction) used (use optimistic locking based on object version, same way as hibernate optimistic locking). 
NOTX mode disables transactions.

For example, to switch off transactions use:

```java
install(new OrientModule(url, user, password)
                .defaultTransactionConfig(new TxConfig(OTransaction.TXTYPE.NOTX));
```

## Custom orient types

You may need to use orient [custom types](https://orientdb.org/docs/3.1.x/java/Object-2-Record-Java-Binding.html)
(custom converter to/from object, used *only in object connection*).

To register custom type:

```java
install(new OrientModule(url, user, password)
                .withCustomTypes(MyTypeSerializer.class));
```

where `MyTypeSerializer implements OObjectSerializer`

Serializers are assumed to be guice beans: declare it in guice module if required, otherwise guice JIT will be used to 
obtain converter instance.

Custom converters are collected using guice multibinder feature, so you can also install custom converter by using
multibinder directly:

```java
Multibinder.newSetBinder(binder(), OObjectSerializer.class)
        .addBinding().to(serializerClass);
``` 

## Scheme initialization

To initialize (or migrate) scheme register implementation of

```java
ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer

```

Example:

```java
install(new OrientModule(url, user, password));
bind(SchemeInitializer.class).to(MySchemeInitializer.class);
```

Scheme initializer is called in NOTX unit of work (orient requires database scheme updates to be performed without transaction).

By default, no-op implementation enabled.

### Object scheme mapping

If you use object api you can use provided object scheme mapper to create (or update) database
from pojo beans (JPA like).

!!! tip
    it is also able to mix object mapping with graph types, so you can mix object and graph apis
    (see [@EdgeType](../mapping/class/edge.md) and [@VertexType](../mapping/class/vertex.md)). 

See [object scheme mapper guide](../mapping/objectscheme.md) for details. 

!!! note
    Custom migration annotations could be [easily added](../mapping/writing.md).

## Data initialization

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


## Lifecycle

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
