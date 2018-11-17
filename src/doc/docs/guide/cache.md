# Cache

Three caches used:

* Scheme object mapping cache
* Repository descriptors
* Generics cache

## Scheme object mapping cache

When predefined `PackageSchemeModule` or `AutoScanSchemeModule` [object mapping](../mapping/objectscheme.md) modules used or `ObjectSchemeInitializer` used directly, mapped objects are cached.

The is required to avoid redundant computations for registering same types: if models use some base class, then base class would be registered multiple times (for each entity) without cache.

Normally, cache should not cause problems, because scheme is initialized on application start and doesn't changed anymore.

Cache may be cleared at any time:

```
@Inject ObjectSchemeInitializer initializer;
...
initializer.clearModelCache();
```

## Repository descriptors

[Repository](../repository/overview.md) descriptors computed on first repository method execution and cached to speed up future method executions.

If you use JRebel or other class reloading tool (maybe some other reason) you will need to disable descriptors caching.

To do it set system property or environment variable:

```
ru.vyarus.guice.persist.orient.repository.core.MethodDescriptorFactory.cache=false
```

Or from code:

```java
MethodDescriptorFactory.disableCache();
```

Also you can clear cache manually (on instance):

```java
@Inject MethodDescriptorFactory factory;
...
factory.clearCache()
```

!!! note 
    Disabling descriptors case, also disables generics resolution cache.

## Generics cache

External library used for generics resolution: [generics-resolver](https://github.com/xvik/generics-resolver).
It maintains resolved class [generics cache](https://github.com/xvik/generics-resolver#cache) (to avoid resolution for same classes).

!!! note 
    Disabling descriptors cache will disable generics cache, so usually you don't need to know about it.