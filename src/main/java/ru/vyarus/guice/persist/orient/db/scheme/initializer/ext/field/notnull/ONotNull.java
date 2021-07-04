package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.notnull;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to mark property as not null (or remove notnull marker).
 * Name changed to avoid collision with javax validation.
 * <p>
 * Sample sql: alter property Model.name notnull true.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Property.html">docs</a>
 * @since 09.03.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(NotNullFieldExtension.class)
public @interface ONotNull {

    /**
     * @return true to make property not null, false to allow null values
     */
    boolean value() default true;
}
