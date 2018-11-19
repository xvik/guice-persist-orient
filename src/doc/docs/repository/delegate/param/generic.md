# @Generic

!!! summary ""
    Delegate method param extension

Used to reference caller repository method generic.

For example, we have [mixin](../../mixins.md) definition:

```java
public interface SomeMixin<T> {
   @Delegate(SomeMixinDelegate.class)
   List<T> findAll()
}

@ProvidedBy(DynamicSingletonProvider.class)
public abstract class SomeMixinDeleagate implements SomeMixin {
    public List<Model> findAll(@Generic("T") Class model) {
        ...
    }
}
```

Now when we use mixin in repository:

```java
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends SomeMixin<Model> {}
```

When we call mixin method (`findAll`) on repository, it will delegate to bean method with `Model.class` as parameter (resolved generic).

By default, generic is resolved from mixin method definition class (`SomeMixin` in example above). In more complex cases you may need to know generic from some other type (and you sure this type is present in calling repository hierarchy). To do it specify type in annotation:

```java
List findAll(@Generic(value = "T", genericHolder = SomeOtherMixin.class) Class model) {...}
```

See [ObjectCrud](../../mixin/objcrud.md) and [DocumentCrud](../../mixin/doccrud.md) mixins for usage examples.
