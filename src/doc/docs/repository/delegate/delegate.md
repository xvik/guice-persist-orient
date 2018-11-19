# @Delegate method

!!! summary ""
    Delegate method extension

[Delegate methods](../delegatemethods.md) delegate execution to other guice bean method. 

```java
@Delegate(TargetBean.class)
List<Model> selectSomething();
```
