# @EdgeType

!!! summary ""
    Scope: class

Register class as [edge type](https://orientdb.org/docs/3.1.x/java/Graph-VE.html#edges). On scheme level it means root class must extend E

So in simple case `YourModel extends E`, in hierarchy case `YourModel extends YourBaseModel extends E`

Defined on model type:

```java
@EdgeType
public class MyModel {...}
```

If class already registered and its root type does not have assigned superclass, it would be changed to edge type, otherwise error thrown.

!!! note 
    Since orient 2.1 multiple inheritance is available and annotation will assign superclass *directly for current class*. If any class in hierarchy already extends E then nothing will be done. If any class in hierarchy extends V exception will be thrown (class can't be edge and vertex at the same time)
