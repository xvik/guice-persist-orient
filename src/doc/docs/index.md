# Welcome to guice-persist-orient

!!! summary ""
    [Guice](https://github.com/google/guice) integration for [OrientDB](https://orientdb.com/).

**[Release notes](about/release-notes.md)** - [Support](about/support.md) - [License](about/license.md)

## Main features

* Integration through [guice-persist](https://github.com/google/guice/wiki/GuicePersist) (UnitOfWork, PersistService, @Transactional)
* Support for [document](https://orientdb.org/docs/3.1.x/java/Document-Database.html), [object](https://orientdb.org/docs/3.1.x/java/Object-Database.html) and
[graph](https://orientdb.org/docs/3.1.x/java/Graph-Database-Tinkerpop.html) databases
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


!!! note
    Orient team tries to move focus from object api (jpa-like) into document and graph apis. 
    Library allows you to use pure document and graph apis, but also provides advanced tools
    to [mix object and graph apis](repository/mixins.md). So you can keep model as pojos and connect them with type safe (also pojo) edges.

## Documentation structure

Library consists of 3 parts:

* [Core orient integration](guide/configuration.md) (user guide section) 
* [Object schema mapper](mapping/objectscheme.md) (mapping section) - used for schema creation from pojos during startup
* [Repositories framework](repository/overview.md) (repository section) - spring-data like approach of query definitions with annotations 

Start reading from [getting started](getting-started.md) guide for introduction.

All core apis are described on [API](api.md) page.