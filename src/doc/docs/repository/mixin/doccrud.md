# DocumentCrud

Provide basic crud operations for document connection. 
Usage:

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends DocumentCrud<Model> {}
```

It relies on model class to resolve model type, but, as described above, even empty class may be used (just to define scheme class name).
