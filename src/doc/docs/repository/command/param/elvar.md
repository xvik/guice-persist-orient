# @ElVar

!!! summary ""
    Command method param extension

Marks parameter as [el variable](../../commandmethods.md#el-variables) (variables are substituted in query before execution).

```java
@Query("select from Model where ${prop} = ?")
List<Model> findBy(@ElVar("prop") String prop, String value);
```

Any type could be used for variable. Value is converted to string using `object.toString`.
Null value converted to empty string ("").

If `Class` used as variable then only class name will be used, for example:

```java
@Query("select from ${model}")
List<Model> findAll(@ElVar("model") Class model);
```

It is safe to use `Class`, `Enum`, `Number` (int, long etc), `Character` types, because they not allow sql injection.
But when string or raw object used as value, you can define a list of allowed values to avoid injection:

```java
@Query("select from Model where ${prop} = ?")
List<Model> findAll(@ElVar(value = "prop", allowedValues = {"name", "nick"}) String prop, String value);
```

Now if provided value is not "name" or "nick" exception will be thrown.

If you use String variable without list of allowed values, warning will be shown in log (possible injection).
If you 100% sure that it's safe, you can disable warning:

```java
@Query("select from Model where ${cond}")
List<Model> findWhen(@ElVar(value = "cond", safe = true) String cond);
...
repository.findWhen("name='luke' and nick='light'")
```

Also, safe marker documents method safety (kind of "yes, I'm sure").
