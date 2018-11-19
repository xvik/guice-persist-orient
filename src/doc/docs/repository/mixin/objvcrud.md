# ObjectVertexCrud

Provide basic crud operations for vertex objects: objects annotated with [@VertexType](../../mapping/class/vertex.md) or 
simply `extends V` in scheme. This mixin mixes Object and Graph apis to let you correctly work with graph vertexes through object api.

It has the same api as [ObjectCrud](objcrud.md) (internally they both extend the same `BaseObjectCrud` mixin). 

!!! warning
    It is important to use this specific version because it uses graph api for remove: if object api directly used, graph consistency is not checked and so edges leading to/from this vertex are not removed.  

It also contains special methods for conversion between object and graph api: `vertexToObject` and `objectToVertex`.

```java
@VertexType
public class Model {}

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends ObjectVertexCrud<Model> {}
``` 

You can use it together with [EdgesSupport](edges.md) or [EdgeTypeSupport](edgetype.md) to add relevant edges operations into repository.

For example, suppose we want to always connect `Model` objects with `ModelConnection` edge type:

```java
@EdgeType
public class ModelConnection {}

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends ObjectVertexCrud<Model>, 
                       EdgeTypeSupport<ModelConnection, Model, Model> {}
``` 

Now we can operate on objects and connect them with the same repository:

```java
@Inject ModelRepository repository;
...
Model from = repository.save(new Model(..));
Model to = repository.save(new Model(..));
ModelConnection edge = repository.createEdge(from, to);

// we may update edge object properties (if it contains it)
edge.setComment("important connection");
repository.updateEdge(edge);
``` 