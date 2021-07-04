# Transactions

## Unit of work (transaction)

Unit of work defines transaction scope. Actual [orient transaction](https://orientdb.org/docs/3.1.x/internals/Transactions.html) will start only on first connection acquire (so basically, unit of work
may not contain actual orient transaction, but for simplicity both may be considered equal).

Unit of work may be defined by:

* `@Transactional` annotation on guice bean or single method (additional `@TxType` annotation allows to define different transaction type for specific unit of work)
* Inject `PersistentContext` bean into your service and use its methods
* Using `TransactionManager` begin() and end() methods.

First two options are better, because they automatically manage rollbacks and avoid not closed (forgot to call end) transactions.

!!! important 
    Orient 2 is more strict about transactions: now ODocument could be created only inside transaction and object proxy
    can't be used outside of transaction.

!!! warning
    When you get error like    
    ```
    Database instance is not set in current thread. Assure to set it with: ODatabaseRecordThreadLocal.instance().set(db)
    ```    
    It almost certainly means that you perform transactional operation outside of transaction. Simply enlarge transaction scope.    
    
    When you inside transaction, `activateOnCurrentThread()` is called each time you obtain connection from raw connection provider or PersistentContext
    and you will always have correctly bound connection.    
    
    For example, even document creation (`new ODocument()`) must be performed inside transaction.

### Examples

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

!!! note 
    In contrast to spring default proxies, in guice when you call bean method inside the same bean, annotation interceptor will still work.
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

## External transaction

In some rear cases it is required to use already created database object (use thread-bound connection inside guice).
This is possible by using special `TxConfig.external()` config.

```java
// transaction opened manually
ODatabaseDocumentTx db = new ODatabaseDocumentTx();
// database object must be bound on current thread (orient did this automatically 
// in most cases, so direct call is not required)
db.activateOnCurrentThread();

// context is PersistentContext, but TxTemplate may be called directly
context.doInTransaction(TxConfig.external(), () -> {
    // here we can use external transaction          
})

// connection closed manually
db.close();
```

!!! important
    In contrast to normal transaction, guice will not manage commits and rollbacks:
    it is assumed that connection lifecycle is correctly managed manually.

There are intentionally no shortcuts for starting external unit of work because its not supposed
to be used often and must be applied only in cases where other behaviour is impossible.

## Retry

Due to orient implementation specifics, you may face [OConcurrentModificationException](https://orientdb.org/docs/3.1.x/misc/Troubleshooting-Java.html#oconcurrentmodificationexception-cannot-update-record-xy-in-storage-z-because-the-version-is-not-the-latest-probably-you-are-updating-an-old-record-or-it-has-been-modified-by-another-user-dbva-yourvb).

Such exception would be ok for optimistic locking check on object save (object contains version and if db version is different
then your object considered stale, and you cant save it).

But, for example, even query like this may fail:

```
update Model set name = 'updated'
```

In concurrent environment this query also may cause `OConcurrentModificationException` 
(other transaction changed object version in between of model update: action must be repeated).

There is a special base class for such exceptions: `ONeedRetryException`.
So by design some operations may fail initially, but succeed on repeated execution.

To fix such places you can use `@Retry` annotation. It catches exception and if its `ONeedRetryException` (or any cause is retry exception)
it will repeat method execution.

```java
@Retry(100)
@Transactional
public void update()
```

!!! important
    Annotation must be defined outside of transaction, because retry exception is casted on commit and it's impossible to
    catch it inside transaction. If `@Retry` annotated method will be executed under transaction, it will fail (catches redundant definition).
    So be careful using it: be sure not to use annotated methods in transaction.

`@Retry` may be used with `@Transactional` on the same method (retry applied before).

In some cases using script instead of query solves concurrent update problem (even without retry):

```
begin
update Model set name='updated'
commit
```

Anyway, always write concurrent tests to be sure.

