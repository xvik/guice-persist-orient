# @RenamePropertyFrom

!!! summary ""
    Scope: property

Renames existing scheme property before class registration.
If you rename property, orient scheme mapper simply creates new property, so if you have data
in db it will not be visible for new property.

```java
public class MyModel {
   @RenamePropertyFrom("foo")
   private String bar;
}
```

If specified old property does not exist, no action will be performed.

If both properties exist error will be thrown.