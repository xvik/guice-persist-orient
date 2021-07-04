# Guice integration for OrientDB
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.com/xvik/guice-persist-orient.svg?branch=master)](https://travis-ci.com/xvik/guice-persist-orient)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/guice-persist-orient?svg=true)](https://ci.appveyor.com/project/xvik/guice-persist-orient)
[![codecov](https://codecov.io/gh/xvik/guice-persist-orient/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/guice-persist-orient)

**DOCUMENTATION**: http://xvik.github.io/guice-persist-orient/

> **[Examples repository](https://github.com/xvik/guice-persist-orient-examples)** 

Support: [Gitter chat](https://gitter.im/xvik/guice-persist-orient)

`guice-persist-orient` wins 4th place in [Software Quality Award 2015](http://www.yegor256.com/2015/04/16/award.html)

### About

[OrientDB](http://orientdb.com/orientdb/) is document, graph and object database (see [intro](https://www.youtube.com/watch?v=o_7NCiTLVis) and [starter course](http://orientdb.com/getting-started/)).
Underlying format is almost the same for all database types, which allows us to use single database in any way. For example, schema creation and updates
may be performed as object database (jpa style) and graph api may be used for creating relations.

Features:
* For orient 3.1 (java 8)
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

### Thanks to

* [Derric Gilling](https://github.com/dgilling) for help with playframework compatibility

### Setup

[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/guice-persist-orient.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/guice-persist-orient)

Maven:

```xml
<dependency>
    <groupId>ru.vyarus</groupId>
    <artifactId>guice-persist-orient</artifactId>
    <version>4.1.0</version>
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
implementation 'ru.vyarus:guice-persist-orient:4.1.0'
//implementation "com.orientechnologies:orientdb-object:3.1.12"
//implementation "com.orientechnologies:orientdb-graphdb:3.1.12"
```

Commented imports required to enable object and graph db support.

OrientDB | Guice | guice-persist-orient
----------|---|------
3.1 | 5.0.1 | [4.1.0](http://xvik.github.io/guice-persist-orient/4.1.0)
3.0 | 5.0.1 | [4.0.0](http://xvik.github.io/guice-persist-orient/4.0.0)
2.2 | 4.2.0 | [3.3.2](http://xvik.github.io/guice-persist-orient/3.3.2)
2.1 | 4.1.0 | [3.2.0](https://github.com/xvik/guice-persist-orient/tree/orient-2.1.x)
2.0 | 4.0.0 | [3.1.1](https://github.com/xvik/guice-persist-orient/tree/orient-2.0.x)
1.0 | 4.0.0 | [2.1.0](https://github.com/xvik/guice-persist-orient/tree/orient-1.x)

NOTE: It's very important for object db to use exact `javassist` version it depends on. If other libraries in 
your classpath use `javassist`, check that newer or older version not appear in classpath.

##### Snapshots

You can use snapshot versions through [JitPack](https://jitpack.io):

* Go to [JitPack project page](https://jitpack.io/#xvik/guice-persist-orient)
* Select `Commits` section and click `Get it` on commit you want to use (top one - the most recent)
* Follow displayed instruction: add repository and change dependency (NOTE: due to JitPack convention artifact group will be different)

### Usage

Read [documentation](https://xvik.github.io/guice-persist-orient/)

### Might also like

* [generics-resolver](https://github.com/xvik/generics-resolver) - extracted library, used for generics resolution during finders analysis
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

---
[![java lib generator](http://img.shields.io/badge/Powered%20by-%20Java%20lib%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-lib-java)
