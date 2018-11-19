# ObjectCrud

Provide basic crud operations for object connection. 
Usage:

```java
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends ObjectCrud<Model> {}
```

You can use `objectToDocument` and `documentToObject` methods for conversion to/from documents.

!!! note 
    Don't use it with vertex objects (annotated with [@VertexType](../../mapping/class/vertex.md)). 
    Use [ObjectVertexCrud](objvcrud.md) instead.
