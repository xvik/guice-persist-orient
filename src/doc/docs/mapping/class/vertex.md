# @VertexType

!!! summary ""
    Scope: class

Register class as [vertex type](https://orientdb.org/docs/3.1.x/java/Graph-VE.html#vertices). On scheme level it means root class must extend V

So in simple case `YourModel extends V`, in hierarchy case `YourModel extends YourBaseModel extends V`

Defined on model type:

```java
@VertexType
public class MyModel {...}
```

If class already registered and its root type does not have assigned superclass, it would be changed to vertex type, otherwise error thrown.

!!! note 
    since orient 2.1 multiple inheritance is available and annotation will assign superclass *directly for current class*. 
    If any class in hierarchy already extends V then nothing will be done. If any class in hierarchy extends E exception will be thrown (class can't be edge and vertex at the same time)
