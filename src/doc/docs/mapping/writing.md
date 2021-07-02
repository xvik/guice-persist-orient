# Writing custom annotations

Two types of extensions supported: type and field (class and property) extensions.

Extensions are resolved from annotations: extension annotation must be annotated with one of: 

* `@SchemeTypeInit` 
* `@SchemeFieldInit` 

This annotation contains extension class, which is instantiated by guice.

No explicit registration required for extension: it's always resolved from found annotation.

Both type and field extension interfaces (`TypeExtension`, `FieldExtension`) contains before and after methods.
Before called before registering class with orient default mechanism and after - after registration.

Extensions are ordered according to `@Order` annotation on extension class.

Consider using singleton scope extensions. If you will choose prototype scope, then single extension instance will be used for both before and after calls. But different instance will be used for different model classes.

`SchemeDescriptor` object is passed to all extension methods and contains some basic context info. 
Extension may need to modify descriptor state: e.g. if extension drops existing class in before method, it must set `descriptor.initialRegistartion = true`.

All provided annotations are extensions. Use them as examples.

`AbstractObjectInitializer` may be used as base class for custom classpath scan model resolution strategy
(instead of two provided).

## Example

As an example, let's take exisitng `ONotNull` annotation:

```java
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(NotNullFieldExtension.class)
public @interface ONotNull {

    boolean value() default true;
}
```

Implementation:

```java
@Singleton
public class NotNullFieldExtension implements FieldExtension<ONotNull> {
    private final Logger logger = LoggerFactory.getLogger(NotNullFieldExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Field field, final ONotNull annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Field field, final ONotNull annotation) {
        final String name = field.getName();
        final boolean notnull = annotation.value();
        final OProperty property = db.getMetadata().getSchema()
                .getClass(descriptor.schemeClass).getProperty(name);
        if (property.isNotNull() != notnull) {
            property.setNotNull(notnull);
            logger.debug("Set {}.{} property notnull={}", descriptor.schemeClass, name, notnull);
        }
    }
}
```

That's all: no explicit registrations reqired (assuming that object mapper is [registered as schema initializer](objectscheme.md#setup)). 

When we use annotation on bean:

```java
public class Model {
    @ONotNull
    private String foo;
}
```

`ObjectSchemeInitializer` will ananlyse bean, find `ONotNull` annotation and detect that
it's annotated with `SchemeFieldInit`. Specified extension class is instantiated with guice and used.
