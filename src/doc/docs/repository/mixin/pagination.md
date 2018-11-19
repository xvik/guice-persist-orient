# Pagination

`Pagination` mixin provides simple pagination for your object entity or document (but document should have reference type, at least to specify schema type name (may be empty class)).

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface MyEntityRepository extends ObjectCrud<MyEntity>, Pagination<MyEntity, MyEntity> {}
...
// return page
Page page = repository.getPage(1, 20);
```

Pagination provides 3 methods:

* `getPage` - receive composed page object
* `getAll` - paginated data selection
* `getCount` - count of all entities

## Implementation

Pagination implementation is a very good example mixin:

Base class `PageSupport` is delegate (to `PageSupportDelegate`), which incapsulates Page object logic.
`Pagination` interface extends it (`Pagination<M, R> extends PageSupport<R>`) and defines query methods (for actual data and entore count).

PageSupportDelegate use reference to repository to call these methods:

```java
public Page getPage(@Repository final Pagination repository, final int page, final int pageSize)
```

Assuming `Pagination` interface will be always used and so delegate could be sure about repository type (implementing just `PageSupport` in repository would make no sense).