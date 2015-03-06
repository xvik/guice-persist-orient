package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Model scheme type extension to register model as vertex type (extends V). If model scheme is already create
 * in database, alters class to extend V (without data loss).
 * <p>Keeps track of object hierarchy: if annotated entity extend some other entity (e.g. VersionedEntity) then this
 * base class must extend vertex type (V) and initializer will try to do it.</p>
 * <p>It's more safe to only annotate topmost classes.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 03.08.2014
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(VertexTypeExtension.class)
public @interface VertexType {
}
