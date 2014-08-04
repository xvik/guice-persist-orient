package ru.vyarus.guice.persist.orient.db.scheme.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate model class to create scheme which extends E and so allowed for creation from graph api (as edge).
 * Keep track of object hierarchy: if annotated entity extend some other entity (e.g. VersionedEntity) then this
 * base class must extend edge type (E) and initializer will try to do it.
 * <p>It's more safe to only annotate topmost classes.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 03.08.2014
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface EdgeType {
}
