package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to mark property as case insensitive (or reset default collate).
 * Making property case insensitive means that all comparisons in sql with this property would be case insensitive.
 * <p>
 * Sample sql: alter property Model.name collate ci.
 * <p>
 * Also, this annotation will affect indexes on this field (will make them ci too). So to create case insensitive
 * index it is required to use this annotation together with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.Index} (
 * <a href="https://orientdb.org/docs/3.1.x/indexing/Indexes.html#case-insensitive-matching-with-indexes">see doc</a>).
 * In orient ci marker always affects both property and index, so it's impossible to create default collate
 * property and ci index on it (because sql engine will
 * <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Introduction.html#automatic-usage-of-indexes">use index
 * automatically</a>).
 * Using field annotation for both cases should remove confusion.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Alter-Property.html">docs</a>
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
