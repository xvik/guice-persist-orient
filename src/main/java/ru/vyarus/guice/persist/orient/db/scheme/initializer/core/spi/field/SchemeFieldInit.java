package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker annotation for model scheme field extensions. Extension annotations must target field only.
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface SchemeFieldInit {

    /**
     * Extension is obtained from guice context. If prototype scope used, different instances will be used for each
     * model (but same instance will be used for before/after calls for processing class).
     * @return extension class
     */
    Class<? extends FieldExtension> value();
}
