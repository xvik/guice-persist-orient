# Api reference

!!! summary ""
    Page describes core api classes. It does not cover [object mapping](mapping/objectscheme.md) or 
    [repositories](repository/overview.md).

Core api classes are:

* `TransactionManager` transaction (unit of work) manager 
* `Provider<connection type>` provides thread bound connection object inside unit of work
* `TxTemplate<connection type>` transactional template simplifies transaction handling (used internally by `@Transactional` annotation)
* `SpecificTxTemplate<connection type>` special version of tx template with connection object passing to callback
* `UserManager` allows changing user outside or inside transaction
* `DatabaseManager` is responsible for lifecycle and pools initialization, may be useful only for loaded types check

`PersistentContext` combines all most used apis for simplified usage (no need to remember everything, just one class). It's not used internally and exist just for public usage. Could be replaced (or extended).

Core annotations:

* [@Transactional](guide/transactions.md) - defines transaction scope
* [@TxType](guide/transactions.md#examples) - used with transaction annotation to specify transaction type
* [@Retry](guide/transactions.md#retry) - catches and retries methods failed with orient ONeedRetryExcepion

## Transaction manager

Unit of work manager. Used to start/stop/rollback transaction. Used internally by all other tx api.

In most cases, it shouldn't be used directly for transaction definition to avoid try-catch code and avoid possibility of not closing transaction. Use TxTemplate or SpecificTxTemplate for manual transaction execution.

The most useful method is current transaction check:

```java
transactionManager.isTransactionActive()
```

Default transaction manager implementation may be substituted in guice module:

```java
bind(TransactionalManager.class).to(MyTransactionalManager.class)
```

When `@Transactional` annotation used, transaction type may be defined with `@TxType` annotation (but in most cases it is not required).

## Connection provider

Connection could be acquired only inside transaction (unit of work).

Possible connection objects:

* `Provider<ODatabaseObject>` for object database connection
* `Provider<ODatabaseDocument>` for document database connection
* `Provider<OrientBaseGraph>` for graph database connection (transactional or not)
* `Provider<OrientGraph>` for transactional graph database connection (will fail if notx transaction type)
* `Provider<OrientGraphNoTx>` for non transactional graph database connection (will provide only for notx transaction type, otherwise fail)

!!! note
    You can't use both `OrientGraph` and `OrientGraphNoTx` in the same transaction (type must be used according to transaction type).
    `OrientBaseGraph` may be used in places where both types are possible (it's the base class for both).

Connection provider logic is implemented in [pools](guide/connections.md#pools) (`PoolManager`). Pool is registered for each connection type (document, object, graph).

!!! important 
    Connection instance is thread bound, so you may be sure that every time `provider.get()` returns same connection object instance (within transaction).

## Transaction templates

Transaction template abstract work with transaction manager (begin/commit/rollback staff).

`TxTemplate` is the core transaction abstraction, used by all other api (`@Transactional` annotation, `SpecificTxTemplate`).

Sample usage:

```java
@Inject TxTemplate<ODatabaseObject> template;

...
template.doInTransaction(new TxAction<Void>() {
        @Override
        public Void execute() throws Throwable {
            // something
            return null;
        }
    });
```

Custom transaction config:

```java
template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), new TxAction<Void>() {
        @Override
        public Void execute() throws Throwable {
            // something
            return null;
        }
    });
```

If template is used inside transaction, it's config is ignored.

`SpecificTxTemplate` is `TxTemplate` but with connection object in callback (to avoid calling connection provider inside callback).

For example:

```java
@Inject SpecificTxTemplate<ODatabaseObject> specificTxTemplate;
...
specificTxTemplate.doInTransaction(new SpecificTxAction<Object, ODatabaseObject>() {
        @Override
        public Object execute(ODatabaseObject db) throws Throwable {
            // something
            return null;
        }
    })
```

It also may be called with custom transaction config.

`TxConfig` may also configure exception types which will not prevent commit or which should trigger rollback (see guice-persist `@Transactional` annotation javadoc for more details).

## User manager

`UserManager` allows you to change connection user. Change may be performed outside of transaction (to affect multiple transactions) or inside transaction (e.g. to check orient security).

To change user for multiple transactions:

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

Overriding may be used in scheme initialization to use more powerful user or to use [orient security model](https://orientdb.org/docs/3.1.x/security/Security.html)
(in this case overriding may be done, for example in servlet filter).

Nested user override is not allowed (to avoid confusion). But you can use user overriding inside transaction.


### Transaction user

To change user inside transaction:

```java
userManager.executeWithTxUser('user', new SpecificUserAction<Void>() {
    @Override
    public Void execute() throws Throwable {
        // do work
    }
})
```

Must be called inside transaction. Changes user inside current connection using `ODatabase.setUser(user)` api.
This change will affect security checks (probably the most common use case).
Nested user override is not allowed (for simplicity).

!!! note
    `PersistentContext` provides shortcut only for changing user inside transaction.

## Database manager

Database manager is responsible for lifecycle (start/stop) and pools management.

The only useful methods:

```java
@Inject DatabaseManager dbManager;
...
// check if database type supported
dbManager.isTypeSupported(DbType.OBJECT)
// all supported types
dbManager.getSupportedTypes()
```

Database types support is driven by classpath (e.g. if orient-graph dependency is not available, no graph db support will be in runtime).

Also, provides access for `OrientDB` object (new orient api) used internally:

```java
OrientDB orient = dbManager.get()
```

The same object could be simply injected with

```java
@Inject Provider<OrientDB>
```

Object might be used for opening direct orient connections (not managed by guice).
Quite rare case.

### Database credentials

You can get used database credentials through `OrientDBFactory` object:

```java
@Inject OrientDBFactory info;
...

String dbName = info.getDbName();
```

This might be useful in tests to drop context database like:

```java
void cleanup() {
    persistService.stop();
    // create new connection object after main connection shut down
    OrientDB db = info.createOrientDB()
    if (db.exists(info.getDbName())) {
        db.drop(info.getDbName())
    }
    db.close()
}
```
