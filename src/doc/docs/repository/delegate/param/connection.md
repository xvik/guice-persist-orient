# @Connection

!!! summary ""
    Delegate method param extension

Used to reference connection object, [selected for repository method](../../internals.md#repository-executor). May be used to
avoid writing redundant providers injection and use direct connection from method argument.

```java
List doSomething(@Connection OObjectDatabaseTx db) {...}
```

Also, may be used for object/document [mixins](../../mixins.md): object and document connections share common abstraction (`ODatabaseInternal`) which may be used to write generic logic for both connections.

```java
List doSomething(@Connection ODatabaseInternal db) {...}
```

Even `Object` may be used as type, to accept any connection type and, for example, branch logic inside method according to connection object type:

```java
List doSomething(@Connection Object db) {...}
```