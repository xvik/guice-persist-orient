# @RenameFrom 

!!! summary ""
    Scope: class

Renames existing scheme class before class registration.
This is important when you rename class to preserve all data under new class name.

!!! note
    Orient ignores package, so changing model package doesn't need rename.

Suppose your class was named `MyModel` and you rename it to `MyCoolModel`.
To properly migrate data:

```java
@RenameFrom("MyModel")
public class MyCoolModel {...}
```

If `MyModel` doesn't exist in scheme, no action will be performed. If both classes exist in scheme, error will be thrown.