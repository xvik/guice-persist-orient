package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.readonly;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to mark property as read only (or remove readonly marker).
 * <p>
 * Sample sql: alter property Model.name readonly true.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Property.html">docs</a>
 * @since 09.03.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(ReadonlyFieldExtension.class)
public @interface Readonly {

    /**
     * @return true to make property readonly, false to allow modification
     */
    boolean value() default true;
}
