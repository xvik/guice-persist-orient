# {{ gradle.version }} Release Notes

!!! important 
    The release is completely working, but repositories part was not migrated to the new api
    and use commands api as before (now deprecated).

    Manual indexes creation was deprecated (assumed to be completely removed), but 
    index creation extensions are completely working now.

    Overall, orientdb is going to drop object database support (most likely in version 4),
    so all related features will have to be removed. I hope [youtrackdb](https://youtrackdb.io/) 
    would be already released at that time providing new object api (and so most object-related
    features could be ported to a new youtrackdb integration module).

Release targets orientdb 3.2 compatibility and guice 7 (jakarta.inject).   
No api or behavior changes.

[Orient 3.2 release notes](https://orientdb.dev/docs/3.2.x/release/3.2/What-is-new-in-OrientDB-3.2.html))

If migrating from orinetdb 2.x, see guice-persist-orient [4.0.0 release notes](http://xvik.github.io/guice-persist-orient/4.0.0/about/release-notes/) 
for orient 3 related updates.

## No default users

!!! warning
    Orient 3.2 [does not create default users](https://orientdb.dev/docs/3.2.x/release/3.2/What-is-new-in-OrientDB-3.2.html) anymore.
    This means that you have to create them manually even for tests (memory database).

It would be simpler to revert old behavior at least for tests.
Modify guice module to create default users for memory databases:

```java
    final OrientModule orient = new OrientModule(db.getUri(), db.getUser(), db.getPass());
    // enable default users creation for memory db (for tests)
    // real database users would be created either manually or in DbLifecycle
    if (DBUriUtils.isMemory(db.getUri())) {
        orient.withConfig(OrientDBConfig.builder()
                .addConfig(OGlobalConfiguration.CREATE_DEFAULT_USERS, true)
                .build());
    }
    install(orient);
```

You can also enable old behavior for all cases to always create local database users.

Otherwise, create database manually BEFORE starting persistence service:

```java
        // dbPath is a path to databases directory (/tmp/db/databases/) WITHOUT database name
        // for remote connection it should be server host (localhost)
        try (OrientDB orientDB = new OrientDB(dbPath, OrientDBConfig.defaultConfig())) {
            if (!orientDB.exists(dbName)) {
                log.info("Creating database {}");

                orientDB.execute("create database " + dbName 
                                 + " plocal users ( admin identified by 'adminpwd' role admin)");
                log.info("Database {} created", dbName);
            }
        }
```

## Known issues

Remains the same from version 4.0.0 (due to not migrated repositories):

- Conversion for new OVertex and OEdge objects not supported
- [Streaming api](https://orientdb.dev/docs/3.2.x/java/Java-Query-API.html#streamin-api) not supported
- Live queries unsubscription method might not be called on remote connection in some cases (looks like a bug)
- Functions, executed through object api, might produce incorrect results (with nulls).
  Marker exception would be thrown to indicate this case.

Also, there is a known problem that OrientModule is using object db classes and so it's
not possible to use it without orientdb-object jar.

## Migration

Replace `javax.inject` usages with `jakarta.inject`.