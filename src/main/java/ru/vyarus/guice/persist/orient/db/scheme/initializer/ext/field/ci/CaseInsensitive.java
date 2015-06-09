package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to mark property as case insensitive (or reset default collate).
 * Making property case insensitive means that all comparisons in sql with this property would be case insensitive.
 * <p>Sample sql: alter property Model.name collate ci</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Alter-Property.html">docs</a>
 * @since 09.06.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(CaseInsensitiveFieldExtension.class)
public @interface CaseInsensitive {

    /**
     * @return true to mark property as case insensitive, false to remove case insensitivity
     */
    boolean value() default true;
}
