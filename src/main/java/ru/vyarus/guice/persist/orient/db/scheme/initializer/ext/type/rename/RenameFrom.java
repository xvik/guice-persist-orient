package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.rename;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Model scheme type extension to rename model class name. Class renamed before orient registration,
 * so data will be preserved.
 * <p>
 * If old class not exist in db, no action will be performed.
 * <p>
 * If new class already registered, exception will be thrown, because rename is impossible.
 * <p>
 * Sample sql: alter class OldModel name Model.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Class.html">alter docs</a>
 * @since 07.03.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(RenameFromTypeExtension.class)
public @interface RenameFrom {

    /**
     * @return old class name
     */
    String value();
}
