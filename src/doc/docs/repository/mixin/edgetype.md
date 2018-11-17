# EdgeTypeSupport

Very similar to [EdgesSupprt](edges.md) mixin, but for one concrete edge type. Also, you can restrict from/to types using generics.

It also supposed to be used to supplement [ObjectVertexCrud](objvcrud.md).

```java
@VertexType
public class Model {}

@EdgeType
public class ModelConnection {}

@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface ModelRepository extends ObjectVertexCrud<Model>, 
                       EdgeTypeSupport<ModelConnection, Model, Model> {}
``` 

It uses [EdgesSupport](edges.md) as bean internally and may serve as an example of how to implement more specific edge mixins.
