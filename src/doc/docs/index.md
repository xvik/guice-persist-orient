# Welcome to guice-persist-orient

!!! summary ""
    [Guice](https://github.com/google/guice) integration for [OrientDB](https://orientdb.com/).

[Release notes](about/history.md) - [Support](about/support.md) - [License](about/license.md)

## Main features

* Integration through [guice-persist](https://github.com/google/guice/wiki/GuicePersist) (UnitOfWork, PersistService, @Transactional)
* Support for [document](http://orientdb.com/docs/last/Document-Database.html), [object](http://orientdb.com/docs/last/Object-Database.html) and
[graph](http://orientdb.com/docs/last/Graph-Database-Tinkerpop.html) databases
* Database types support according to classpath (object and graph db support activated by adding jars to classpath)
* All three database types may be used in single transaction (changes will be visible between different apis)
* Hooks for schema migration and data initialization extensions
* Extension for orient object to scheme mapper with plugins support
* Auto mapping entities in package to db scheme or using classpath scanning to map annotated entities
* Auto db creation (for memory, local and plocal)
* Different db users may be used (for example, for schema initialization or to use orient security model), including support for user change inside transaction
* Support method retry on ONeedRetryException
* Spring-data like repositories with advanced features (e.g. generics usage in query). Great abilities for creating reusable parts (mixins). Support plugins.
* Basic crud mixins with ability to use object api for graphs
* [Compatible with Play framework](https://github.com/xvik/guice-persist-orient-play-example)


