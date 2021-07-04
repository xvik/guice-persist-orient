package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Model scheme type extension to register model as edge type (extends E). If model scheme is already created
 * in database, alters class to extend E (without data loss).
 * <p>
 * Note: due to orient multiple inheritance support since 2.1, superclass is assigned directly
 * to annotated class.
 * <p>
 * If class or its super classes extend V error will be thrown. If class or any class in hierarchy
 * already extend E, nothing will be done.
 * <p>
 * It's more safe to only annotate topmost classes.
 * <p>
 * Sample sql: alter class Model superclass E, create class Model extends E.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Class.html">alter docs</a>
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Create-Class.html">create docs</a>
 * @since 03.08.2014
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(EdgeTypeExtension.class)
public @interface EdgeType {
}
