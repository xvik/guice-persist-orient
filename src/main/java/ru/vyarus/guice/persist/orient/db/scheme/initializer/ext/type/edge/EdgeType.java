package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Model scheme type extension to register model as edge type (extends E). If model scheme is already create
 * in database, alters class to extend E (without data loss).
 * <p>Keeps track of object hierarchy: if annotated entity extend some other entity (e.g. VersionedEntity) then this
 * base class must extend edge type (E) and initializer will try to do it.</p>
 * <p>It's more safe to only annotate topmost classes.</p>
 * <p>Sample sql: alter class Model superclass E, create class Model extends E</p>
 *
 * @author Vyacheslav Rusakov
 * @since 03.08.2014
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(EdgeTypeExtension.class)
public @interface EdgeType {
}
