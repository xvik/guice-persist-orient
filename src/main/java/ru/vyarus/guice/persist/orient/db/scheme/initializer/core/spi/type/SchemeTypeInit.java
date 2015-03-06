package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker annotation for model scheme type extension. Extension annotations must target type only.
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface SchemeTypeInit {

    /**
     * Extension is obtained from guice context. If prototype scope used, different instances will be used for each
     * model (but same instance will be used for before/after calls for processing class).
     * @return extension class
     */
    Class<? extends TypeExtension> value();
}
