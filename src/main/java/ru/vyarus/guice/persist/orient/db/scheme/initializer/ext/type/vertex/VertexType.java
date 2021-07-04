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
 * <p>
 * Note: due to orient multiple inheritance support since 2.1, superclass is assigned directly
 * to annotated class.
 * <p>
 * If class or its super classes extend E error will be thrown. If class or any class in hierarchy
 * already extend V, nothing will be done.
 * <p>
 * It's more safe to only annotate topmost classes.
 * <p>
 * Sample sql: alter class Model superclass V, create class Model extends V.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Class.html">alter docs</a>
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Create-Class.html">create docs</a>
 * @since 03.08.2014
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(VertexTypeExtension.class)
public @interface VertexType {
}
