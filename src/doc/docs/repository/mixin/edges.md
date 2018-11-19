# EdgesSupport

Provide basic operations for edge objects: objects annotated with [@EdgeType](../../mapping/class/edge.md) 
or simply `extends E` in scheme. This mixin mixes Object and Graph apis to let you correctly work with graph edges through object api.

It is very convenient to use edge objects even if they have no properties: it's more type safe and you can use hierarchy of edge types for polymorphic queries.

For example, if you have `BaseConnection` and `SpecificConnection extends BaseConnection` then querying for `BaseConnection` type edges will return everything and when required we can query only for `SpecificConnection` to get more specific results (you can see a lot of examples in orient docs with querying edges by base class `E`). This works the same way as with normal objects (vertexes) providing very powerful ability for design.

Mixin intended to be used as supplement to [ObjectVertexCrud](objvcrud.md) (to add edge functions for vertex repository).

!!! note 
    `EdgesSupport` may be used directly (injected as bean). This is because its very generic itself.

## Edges creation

To create new edge we always need two objects (from and to vertexes). You can use objects, vertexes, documents or simply string id's.

```java
@Inject EdgesSupport edges;
...
MyEdgeType edge = edges.createEdge(MyEdgeType.class, from, to)
```

Here new edge of class `MyEdgeType` is created between from and to vertexes. Api returns object instance of edge, which may be used for property updates or some other need.

If edge contains properties, you can create edge from object or document instance:

```java
MyEdgeType edge = edges.createEdge(from, to , new MyEdgeType(...));
```

Orient graph api is hiddent, but you can always convert edge object to orient edge and back: use `edgeToObject` and `objectToEdge` methods.

## Working with edge

In orient, edges are stored as normal objects (they just have special meaning). So you can operate on edge using any api (document, object and graph). But always remove edges with graph api to grant consistency.

Edge creation methods return created edge as object (it's an edge record loaded with object api). This is useful for edge properties updates:

```java
MyEdgeType edge = edges.createEdge(MyEdgeType.class, form, to);
edge.setName("some name");
edges.updateEdge(edge);
```

Method updateEdge is generic and accepts not just object pojo but also OrientEdge instance.

## Searching edge

You can find edge of type by nodes:

```java
MyEdgeType edge = edges.findEdge(MyEdgeType.class, from, to)
```

Or ignoring direction:

```java
MyEdgeType edge = edges.findEdgeBetween(MyEdgeType.class, node1, node2)
```

As with creation, object, document, vertex or rid may be used as node parameters.

!!! note 
    Api returns only first edge! There may be other edges. For example, create method is not checking existing edge between nodes, so you can create many edges (even of the same class) between same nodes.

You can always make your own methods with sql (or maybe custom delegate logic) to implement more specific search cases.

## Removing edges

Edge may be removed directly

```java
edges.deleteEdge(edge);
```

Using object, document, orient edge or rid.

Or you can remove all edges of type (possibly more then one!) between nodes (but only in one direction!):

```java
int cnt = edges.deleteEdge(MyEdgeType.class, from, to);
```