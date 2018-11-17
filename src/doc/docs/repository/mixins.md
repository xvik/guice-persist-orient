# Repository mixins

Mixins are reusable parts, which you can use in repositories.

Interfaces are perfect for writing mixins, because both interface and simple class can implement multiple interfaces and so can use any number of mixins.

Java generics plays major role in mixins: as you read before exact method return types are important for proper connection selection. Also, mixins plays parametrization role for commands or for delegates.

Generics resolution is implemented as separate lib [generics-resolver](https://github.com/xvik/generics-resolver).

## Command mixins

All commands support el variables and mixin class generic names may be used as variables:

```java
public interface SelectMixin<T> {
   @Query("select from ${T}")
   List<T> selectAll();
}
```

!!! note 
    Neither `@Transactional` nor `@ProvidedBy` annotations are not required on mixin, because mixin is not a repository itself, it simply interface.

Now when mixin will be used in repository, actual type could be recognized and used:

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends SelectMixin<Model> {}
```

When method selectAll called from repository, generic resolved and used as variable "Model" in query and return type will be treated as `List<Model>`, allowing correctly select object connection (suppose its registered model type).

Depth of hierarchy doesn't matter for generics resolution. So if you want, you can compose few mixins into new one and use it in repositories, instead of implementing all mixins in each repository:

```java
public interface RepositoryBaseMixin<T> extends Mixin1<T>, Mixin2<T> {}
```

### When object model not used

Suppose you don't want to use object mapping and don't have mapped entities.
You may still use generics for types resolution: create empty classes, named the same as your model scheme entities. Use such generics just for queries parametrization.

```java
public class ModelName {
    // empty pojo, used only to specify model name as generic
}

public interface MyMixin<T> {

    @Query("select from ${T}")        
    ODocument query();
}

public interface Repository extends MyMixin<ModelName> {    
}

```

### Extracting common logic

Imagine you have few different repositories for different entities. 
Some of them have `name` property.

Normally you will have to write select by name query in each repository (can't be moved to some base class, because not all entities contains name).

You can write generic mixin:

```java
public interface NamedEntityMixin<T> {

    @Query(query = "select from ${T} where name=?")
    T findByName(String name);
}
```

Now some repositories could simply implement this interface. 
Such things could potentially greatly improve code reuse in repositories.

## Delegate mixins

Delegates provide generalization mechanism for custom logic.
Usually, such cases are handled by abstract classes (AbstrctDao or something like this).

Comparing to simple bean method call, delegate:

* Provides calling repository method context (selected connection, resolved generics etc)
* Adds support for annotation driven extensions (amend extensions)
* Applies result conversion

The simplest case is implementing base crud operations for repostiory as delegate mixin:

```java
@Delegate(ObjectCrudDelegate.class)
public interface ObjectCrud<T> {
    T get(String id);
    T create();
}

@ProvidedBy(DynamicSingletonProvider.class)
public abstract class ObjectCrudDelegate<T> implements ObjectCrud<T> {
    private final Provider<OObjectDatabaseTx> dbProvider;

    @Override
    public T get(final String id) {
        return dbProvider.get().load(new ORecordId(id));
    }

    public T create(@Generic("T") final Class<T> type) {
        return dbProvider.get().newInstance(type);
    }
}
```

Now it could be used to easily apply common operation for repository:

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends ObjectCrud<Model> {}
```

## Bundled mixins

* [DocumentCrud](mixin/doccrud.md) - crud operations for document repositories
* [ObjectCrud](mixin/objcrud.md) - crud operations for object repositories
* [ObjectVertexCrud](mixin/objvcrud.md) - object crud for using with vertex objects (annotated with @VertexType)
* [EdgesSupport](mixin/edges.md) - general support for edge objects (annotated with @EdgeType)
* [EdgeTypeSupport](mixin/edgetype.md) - support for exact edge object type
* [Pagination](mixin/pagination.md) - pagination support for object or document repositories
