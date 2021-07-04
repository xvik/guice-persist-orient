# Object scheme mapping

Default orient mapping is limited in some cases, and that's why custom mapper implementation provided (extending default mapper).

!!! note 
    This is development time solution to quickly update scheme. Its usage in production is obviously limited.

## Default object scheme mapping

See [orient object mapping documentation](https://orientdb.org/docs/3.1.x/java/Object-2-Record-Java-Binding.html) for object mapping
 ([and general object database page](https://orientdb.org/docs/3.1.x/java/Object-Database.html)).

* Orient ignore package, so class may be moved between packages
* If model class extends some other class it will be also registered as separate scheme class.
* When entity field removed, orient will hold all data already stored in records of that field
* When entity field type changes, orient WILL NOT migrate automatically (you need to handle it manually, using custom scheme initializer or through
[orient studio](https://orientdb.org/docs/3.1.x/studio/)).
* When class renamed orient will register it as new entity and you will have to manually migrate all data
(it's possible to use sql commands to rename entity in scheme)
* To use entity within optimistic transaction, it must have version field (annotated with `@Version`). You should add field manually or extend all entities from provided base class: `VersionedEntity`
* JPA annotations can be used to [define cascades](https://orientdb.org/docs/3.1.x/java/Object-Database.html)
* JPA `@Id` annotation may be used to bind object id (String or Object to bind as RID)

Most useful are id and version mapping:

```java
@Id
private String id;
@Version
private Long version;
```

Version property is required for optimistic transactions (enabled by default).

## Graph types

Graph types are usual types with just one difference: its root class must extend `V` or `E` (for vertex or edge).
You may register scheme class (even with default orient mapper) and later make it as `extends V` and it will be valid vertex type, usable for graph api. 
Extended mapper support this case with `@EdgeType` and `@VertexType` annotations.

## Setup

Mapper is registered as [scheme initializer](../guide/configuration.md#scheme-initialization) bean, but mapper may be used directly too: `ObjectSchemeInitializer`.

Two scheme initializer implementations provided:

* `PackageSchemeInitializer` - use all classes in package to init or update scheme (package should be specified as module constructor argument).
* `AutoScanSchemeInitializer` - search classpath for entities annotated with `@Persistent` annotation and use them to create/update scheme.

They can be enabled by `PackageSchemeModule` and `AutoScanSchemeModule` modules. 

!!! note 
    Modules must be registered together with main `OrientModule`.

For example:

```java
install(new AutoScanSchemeModule("my.model.root.package"));
```


## Mapping annotations

Extended mapper provides additional annotations:

Class:

* [@EdgeType](class/edge.md) - register class as edge type
* [@VertexType](class/vertex.md) - register class as vertex type
* [@RenameFrom](class/rename.md) - renames existing scheme class before class registration
* [@Recreate](class/recreate.md) - drop and create fresh scheme on each start
* [@CompositeIndex](class/compositeindex.md) - creates composite index for class (index span multiple properties)
* [@CompositeLuceneIndex](class/compositeluceneindex.md) - creates composite lucene index for class (index span multiple properties)
* [@DropIndexes](class/dropindexes.md) - drops existing indexes on start

Field:

* [@RenamePropertyFrom](property/renameproperty.md) - renames existing scheme property before class registration
* [@Index](property/index.md) - creates index for annotated field
* [@FulltextIndex](property/fulltextindex.md) - creates fulltext index for annotated field
* [@LuceneIndex](property/luceneindex.md) - creates lucene index for annotated field
* [@Readonly](property/readonly.md) - marks property as readonly
* [@ONotNull](property/notnull.md) - marks property as not null
* [@Mandatory](property/mandatory.md) - marks property as mandatory
* [@CaseInsensitive](property/caseinsensitive.md) - marks property as case insensitive

!!! tip
    New annotations could be implemented [easily as plugins](writing.md).

All annotations are safe: they may remain in model even after performing required action (they will just do nothing). 
This makes possible to use them as simple migration mechanism (but its better to use something stronger for production).

!!! important
    Remember that scheme created from objects maintain same hierarchy as your objects. E.g. if you use provided `VersionedEntity` class as base class for entities, it will be also registered in scheme (nothing bad, you may not notice it).
    But for graphs hierarchies its more important: both vertex and edge objects can't extend same class (root class in hierarchy must extend V or E).
    So if you use `@VertexType` or `@EdgeType` annotations make sure their hierarchy not intersect.

See documentation:

* [Classes](https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Class.html)
* [Properties](https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Property.html)
* [Indexes](https://orientdb.org/docs/3.1.x/indexing/Indexes.html) ([create index](https://orientdb.org/docs/3.1.x/sql/SQL-Create-Index.html))

### How it works

Suppose we have model: 

```java
class Model extends BaseModel
```

When model class is registered (note that this will be performed by scheme module automatically)

```java
objectSchemeInitializer.register(Model.class);
```

Its hierarchy parsed and first `BaseModel` class registered and then `Model` class.

During registration initializer lookups all type and field annotations and execute them before and after orient registration.

To avoid registration of same base classes many times, registered classes are cached and not processed next time.
Usually, scheme initialization is performed on application startup so you should not notice cache presence.
But if you need to register class one more time, you may clear cache manually:

```java
objectSchemeInitializer.clearModelCache();
```

(`ObjectSchemeInitializer` is registered as guice bean and so available for injection)