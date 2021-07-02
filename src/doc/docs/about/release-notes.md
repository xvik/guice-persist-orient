# {{ gradle.version }} Release Notes

!!! warning 
    The release is completely working, but repositories part was not migrated to the new api
    and use commands api as before (now deprecated). I started repositories refactor  (long ago) 
    but have no time to finish it for now. Releasing current state as "better deprecated apis than nothing". 

    If possible 3.1 and maybe 3.2 compatible versions would be released with current apis
    and then repositories refactor would be done (when I would have time for it).

Release target orient 3.0 compatibility.

[Orient 3.0 release notes](http://orientdb.com/docs/3.0.x/release/3.0/What-is-new-in-OrientDB-3.0.html)

Updated to guice 5.0 (but should work with guice 4 too). 

## Major changes

!!! important ""
    Dropped java 1.6 and 1.7 support (orient targets java 8 now).

!!! warning ""
    orientdb-object and orientdb-graphdb are optional dependencies now!

Orient introduced [new unified api](http://orientdb.com/docs/3.0.x/java/Document-API-Database.html) (OrientDB: document + graph) so document connection now provides graph abilities
(for old graph apis import orientdb-graphdb dependency, as before).

Document pool switched into new `ODatabasePool` (new orient pool implementation). As before, 
object and graph apis wrapped around document api (to utilize single connection). 

### Configuration changes

You should now apply `embedded:` prefix instead of `plocal:` (but plocal is still works)

New option allows automatic creation of remote databases:

```java
new OrientModule(,,)
    .autoCreateRemoteDatabase(user, pass, type)
```

To apply custom `OrientDBConfig` (different from `OrientDBConfig.defaultConfig()`) use:

```java
new OrientModule(,,)
    .ithConfig(conf)
```

### API changes

New interfaces used for injectable db connection objects: `ODatabaseObject` and `ODatabaseDocument`
(instead of `OObjectDatabaseTx` and `ODatabaseDocumentTx` respectively).

`@Inject Provider<OrientDB>` might be used for manual connections opening.

`@Inject OrientDBFactory` might be used for accessing database credentials.

### Indexes

`@FullTextIndex` annotation does not have `useHashIndex` option anymore because
`OClass.INDEX_TYPE.FULLTEXT_HASH_INDEX` [was dropped in orient 3.0.38](https://github.com/orientechnologies/orientdb/commit/bfceffa50d3f708f5c1c05dab1f082861df01e12#diff-3371617e7407306ad4397a0835f64175314b828295d5ad88891c051915d2d8aaL226)

### Repositories

Repositories implementation not changed since the last version: it is still uses commands api
(deprecated). It means everything would work as before, but you can't use some new features
(automatic result conversion would not work for new types).

Known repositories issues:
    - Conversion for new OVertex and OEdge objects not supported
    - [Streaming api](Streams not supported) not supported
    - Live queries unsubscription method might not be called on remote connection in some cases (looks like a bug)
    - Functions executed through object api might produce incorrect results (with nulls).
      Marker exception would be thrown in this case.


## Migration guide

### Dependencies

Object and graph dependencies are no longer provided as transitive dependencies, so if you need them then 
specify directly:

```groovy
implementation 'ru.vyarus:guice-persist-orient:4.0.0'
implementation "com.orientechnologies:orientdb-object:3.0.38"
implementation "com.orientechnologies:orientdb-graphdb:3.0.38"
```

### Api changes

Change all injections of `OObjectDatabaseTx` into `ODatabaseObject` and 
`ODatabaseDocumentTx` into `ODatabaseDocument`.

For example:

before:

```java
@Inject
Provider<OObjectDatabaseTx> db;
```

becomes:

```java
@Inject
Provider<ODatabaseObject> db;
```

### Tests

If you were creating or dropping databases in tests (with orient utilities) then use
new db factory instead:

```java
@Inject
OrientDBFactory info

void cleanup() {
    OrientDB db = info.createOrientDB()
    if (db.exists(info.getDbName())) {
        db.drop(info.getDbName())
    }
    db.close()
}
```

To simplify remote database creation you can use:

```java
OrientDBFactory.enableAutoCreationRemoteDatabase(serverUser, serverPassword, dbType)
```

After this remote database would be created automatically on startup.