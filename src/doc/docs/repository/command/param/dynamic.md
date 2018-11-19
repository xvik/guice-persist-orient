# @DynamicParams

!!! summary ""
    Command method param extension

Marks parameter as dynamic command parameters provider.

For positional parameters, parameter type must be `List`, array or vararg.

```java
@Query("select from Model where name=? and nick=?")
List<ODocument> positionalList(@DynamicParams List<String> params)
```

For named parameters use `Map`.

```java
@Query("select from Model where name=:name and nick=:nick")
List<ODocument> namedMap(@DynamicParams Map<String, String> params);
```

Dynamic parameters may be used with static definitions

```java
@Query("select from Model where name=? and nick=?")
List<ODocument> mixPositional(String name, @DynamicParams String... params);
```

Dynamic parameters may be used when it's more comfortable (for any reason) to provide prepared parameters object instead of static parameters binding in method. 
And, of course, when number of parameters is not strict.

```java
@Query('select from Model where ${cond}')
List<ODocument> findWhere(@ElVar("cond") String cond, @DynamicParams Object... params);
```
