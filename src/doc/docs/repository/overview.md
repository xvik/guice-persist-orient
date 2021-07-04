# Repositories

Repository annotations simplify writing dao or repository objects.
Repositories are very close to spring-data repositories and following description will follow this approach.
But repository methods may be used in any way (like dao or as additional methods for beans).

Repositories mainly cover query definitions (removing all boilerplate code).
If you need something like spring-data specifications, you can use [orientqb](https://github.com/raymanrt/orientqb)

Example repository query method:

```java
public interface MyRepository {
    
    @Query("select from Model where name=? and nick=?")
    List<Model> find(String name, String nick);
}
```

Repositories implementation is based on extensions (every annotation you'll see is an extension).
Custom extensions supported, so you can change almost everything.

## Setup

To use repository features register repository module in guice context:

```java
install(new RepositoryModule());
```

## Guice abstract types support

Repository methods defined with annotations, so interface and abstract methods are ideal candidates to use them.
Guice doesn't allow using abstract types, but it's possible with [a bit of magic](https://github.com/xvik/guice-ext-annotations).

Abstract types (abstract class or interface containing repository methods) could be registered directly in guice module:

```java
bind(MyInterfaceRepository.class).to(DynamicClassGenerator.generate(MyInterfaceRepository.class)).in(Singleton.class)
```

Or dynamic resolution could be used (guice JIT resolution):

```java
@ProvidedBy(DynamicSingletonProvider.class)
public interface MyRepository
```

When some bean require this dao as dependency, guice will call provider, which will generate proper class for guice.
(dynamic resolution completely replaces classpath scanning: only actually used repositories will be created)
Note, this will automatically make bean singleton, which should be desirable in most cases. If you need custom scope
use `DynamicClassProvider` with `@ScopeAnnotation` annotation (see details in [guice-ext-annotations](https://github.com/xvik/guice-ext-annotations))

!!! note
    Intellij IDEA will warn you that ProvidedBy annotation is incorrectly typed, but it's ok, because provider is too generic.
    There is nothing I can do with it and it's the best (the simplest) way I know (without explicit classpath scanning, which is redundant).

!!! important 
    Guice will control instance creation, so guice AOP features will completely work!
    `@Transactional` annotation may be used (generally not the best idea to limit transaction to repository method, but in some cases could be suitable).
    You can think of repository interface or abstract class as of usual guice bean (no limitations).

Repository methods are applied using aop (that's why they could be used everywhere).

## Repositories overview

There 2 types of repository methods:

* [Commands](commandmethods.md) - orient data manipulation calls (queries, commands, scripts etc) and build around orient command objects
* [Delegates](delegatemethods.md) - methods delegate execution to some other beans (useful for generic logic)  

| Method annotation | Description |
|---------|----------------------------|
|[@Query](command/query.md) | select/update/insert query |
|[@Function](command/function.md) | orient function call |
|[@Script](command/script.md) | script call (sql, js etc) |
|[@AsyncQuery](command/asyncquery.md) | asynchronous query call |
|[@LiveQuery](command/livequery.md) | orient live query subscription call |
|[@Delegate](delegate/delegate.md) | delegate call to other bean method |


## Defining repository

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository {

    @Query("select from Model")
    List<Model> selectAll();

    @Query("update Model set name = ? where name = ?")
    int updateName(String newName, String oldName);

    @Query("insert into Model (name) values(:name)")
    Model create(@Param("name") String name);
}
```

!!! note 
    Repository methods could be used to supplement existing bean, but suggest to use pure interface repositories.

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class MyDao {

    @Query("select from Model")
    public abstract List<Model> selectAll();

    // normal method
    public void doSomething() {
        ...
    }
}
```

!!! note  
    `@Transactional` is not required (annotation usage depends on your service architecture, but repository method
    must be used inside transaction).


## Usage examples

[Function](https://orientdb.org/docs/3.1.x/admin/Functions.html) call:

```java
@Function("function1")
List<Model> function();
```

Positional parameters:

```java
@Query("select from Model where name=? and nick=?")
List<Model> parametersPositional(String name, String nick)
```

Named parameters:

```java
@Query( "select from Model where name=:name and nick=:nick")
List<Model> parametersNamed(@Param("name") String name, @Param("nick") String nick)
```

[Pagination](https://orientdb.org/docs/3.1.x/sql/Pagination.html):

```java
@Query("select from Model where name=? and nick=?")
List<Model> parametersPaged(String name, String nick, @Skip int skip, @Limit int limit)
```

[El variable](commandmethods.md#el-variables):

```java
@Query("select from Model where ${prop}=?")
List<Model> findBy(@ElVar("prop") String prop, String value)
```

[Fetch plan](https://orientdb.org/docs/3.1.x/java/Fetching-Strategies.html) parameter:

```java
@Query("select from Model")
List<Model> selectAll(@FetchPlan("*:0") String plan);
```

[Sql](https://orientdb.org/docs/3.1.x/sql/SQL-batch.html) script:

```java
@Script("begin" +
  "let account = create vertex Account set name = :name" +
  "let city = select from City where name = :city" +
  "let edge = create edge Lives from $account to $city" +
  "commit retry 100" +
  "return $edge")
Edge linkCity(@Param("name") String name, @Param("city") String city)
```

[Js](https://orientdb.org/docs/3.1.x/js/Javascript-Command.html) script:

```java
@Script(language = "javascript", value =
 "for( i = 0; i < 1000; i++ ){" +
     "db.command('insert into Model(name) values (\"test'+i+'\")');" +
 "}")
void jsScript()
```

[Async](https://orientdb.org/docs/3.1.x/java/Document-API-Documents.html#asynchronous-queries) query:

```java
@AsyncQuery("select from Model")
void select(@Listen OCommandResultListener listener)
```

Type safe listener (with conversion):

```java
@AsyncQuery("select from Model")
void select(@Listen AsyncQueryListener<Model> listener)
```

Or with projection:

```java
@AsyncQuery("select name from Model")
void select(@Listen AsyncQueryListener<String> listener)
```

Dynamic parameters:

```java
@Query('select from Model where ${cond}')
List<ODocument> findWhere(@ElVar("cond") String cond, @DynamicParams Object... params);
```

Non blocking (listener execute in different thread):

```java
@AsyncQuery(value = "select from Model", blocking = false)
Future<List<Model>> select(@Listen AsyncQueryListener<Model> listener)
```

[Delegate](delegatemethods.md) example:

```java
public class SomeBean {
   public List getAll() {
      ...
   }
}

@Delegate(SomeBean.class)
List getAll();
```

[Live](https://orientdb.org/docs/3.1.x/java/Live-Query.html) query:

```java
@LiveQuery("select from Model")
int subscribe(@Listen OLiveResultListener listener)
```

Type safe listener (with conversion): 

```java
@LiveQuery("select from Model")
int subscribe(@Listen QueryResultListener<Model> listener)
```

Or vertex conversion:

```java
@LiveQuery("select from Model")
int subscribe(@Listen QueryResultListener<Vertex> listener)
```

Unsubscription (usual command call):

```java
@Query("live unsubscribe ${token}")
void unsubscribe(@ElVar("token") int token)
```

Read more about method usage:

* [Command methods](commandmethods.md)
* [Delegate methods](delegatemethods.md)

!!! tip
    For more examples see [repository definition examples](https://github.com/xvik/guice-persist-orient-examples/tree/master/repository-examples)

Writing extensions:

* [Extending commands](commandinternals.md)
* [Extending delegates](delegateinternals.md)

## Return types

You can use `Iterable`, `Collection`, `List`, `Set`, any collection implementation, array, single element or `Iterator` as return type.
Conversion between types will be applied [automatically](result.md).

```java
@Query("select from Model")
List<Model> selectAll();

@Query("select from Model")
Set<Model> selectAll();

@Query("select from Model")
Model[] selectAll();

@Query("select from Model")
Iterable<Model> selectAll();

@Query("select from Model")
Iterator<Model> selectAll();
```

If you define single result, when query produce multiple results, first result would be automatically taken:

```java
@Query("select from Model limit 1")
Model selectAll();
```

!!! note 
    Limit is not required, but preferred, as soon as you don't need other results

### Projection

In some cases simple value is preferred, for example:

```java
@Query("select count(@rid) from Model)
int count();
```

Orient returns `ODocument` from query with single field (count).
Default [result converter](result.md) could recognize when document or vertex contain just one property and return only simple value.

Another case is when you select single field:

```java
@Query("select name from Model")
String[] selectNames()
```

Read more about [projection](result.md#result-projection)

### Result type definition

It is very important to always define exact return type. Connection type defines type of result object:
document connection always return `ODocument`, object return mapped objects (but `ODocument` for field calls)
and graph - `Vertex` and `Edge`.

Result type is used internally to detect connection type for query.

For example, if you write:

```java
@Query("select from Model")
List selectAll();
```

You will actually receive `List<ODocument>`, because without generic it's impossible to detect required return type
and document connection used for query.

For example, in this case graph connection would be selected:

```java
@Query("select from Model")
List<Vertex> selectAll();
```

### Result conversion

Every repository method result is converted with default converter (as described above).

You can use more specific result conversion extension, for example:

```java
@Query("select from Model")
@NoConversion
List<Model> selectAll();
```

NoConversion disables conversion mechanism and you receive result object as is.

Read more about [converter mechanism and writing custom converters](result.md).

## Mixins

Java support multiple inheritance for interfaces and you can inherit multiple interfaces in classes.
So interfaces are ideal for writing small reusable parts (mixins).

### Command mixins

El variables in commands support references to class generics, so you can use it for generic repository logic:

```java
public interface MyMixin<T> {

    @Query("select from ${T}")
    List<T> selectAll()
}

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends MyMixin<Model> {}
```

When you call mixin method from repository instance

```java
repository.selectAll()
```

Generic value Model will be used for command `select from Model` and return type will be resolved as `List<Model>`,
which will allow to select proper connection (object if Model is mapped entity).

You may use as many generics as you need. Any repository hierarchy depth will be correctly resolved, so
you can even use composition mixins, which wil simply combine commonly used mixins:

```java
public interface RepositoryBase<T> extends Mixin1<T>, Mixin2<T> {}
```

!!! note 
    You don't need to set `@ProvidedBy` annotation on mixins, because it's just interfaces and they are not used as repository instances.

### Delegate mixins

Delegates are also support generalization through extensions:

```java
public class DelegateBean {
    public List selectAll(@Generic("T") Class model) {

    }
}

public interface MyMixin<T> {
    @Delegate(DelegateBean.class)
    List<T> selectAll()
}
```

When delegate bean called from mixin, it will receive generic value (of calling mixin) as parameter.

Read more about [mixins usage](mixins.md)

### Bundled crud mixins

Crud mixins are the most common thing: commonly these methods are implemented in `AbstractDao` or something like this.

[DocumentCrud](mixin/doccrud.md) mixin provides base crud methods for document repository.

```java
public interface MyEntityDao extends DocumentCrud<MyEntity> {}
```

Set mixin generic value only if you have reference entity class. Generic affects only `getAll` and `create` methods: if generic not set
you will not be able to use only this method.

[ObjectCrud](mixin/objcrud.md) mixin provides base crud methods for object repository:

```java
public interface MyEntityRepository extends ObjectCrud<MyEntity> {}
```

Now MyEntityRepository has all basic crud methods (create, get, delete etc).

[Pagination](mixin/pagination.md) provides simple pagination for your entity or document (but document should have reference type,
at least to specify schema type name (may be empty class))

```java
public interface MyEntityRepository extends ObjectCrud<MyEntity>, Pagination<MyEntity, MyEntity> {}

...
// return page
Page page = repository.getPage(1, 20);
```

In order to use pagination mixin, crud mixin is not required (used in example just to mention one more time that mixins could be combined).
Pagination mixin is the most complex one and good place to inspire how to [write more complex reusable logic](mixin/pagination.md#implementation).

[ObjectVertexCrud](mixin/objvcrud.md), 
[EdgesSupport](mixin/edges.md) and
[EdgeTypeSupport](mixin/edgetype.md) mixins allows using graph features from object api.

```java
@vertexType
public class Model {}

@EdgeType
public class ModelConnection {}

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends ObjectVertexCrud<Model>, 
                       EdgeTypeSupport<ModelConnection, Model, Model> {}
                       
@Inject ModelRepository repository;
...
Model from = repository.save(new Model(..));
Model to = repository.save(new Model(..));
ModelConnection edge = repository.createEdge(from, to);
```

## Validation

You can use [guice-validator](https://github.com/xvik/guice-validator) to apply runtime validation (jsr 303) for repository methods:

```java
@Query("select from Model where name = ?")
@Size(min = 1)
List<Model> select(@NotNull String name)
```

Now this query throw ConstraintViolationException if null provided as parameter or no results returned.

!!! important 
    Register validator module before guice-persist-orient modules!
    This way validation will be checked before @Transactional or repository methods logic.
