package ru.vyarus.guice.persist.orient.finder;

import ru.vyarus.guice.persist.orient.db.DbType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Supplement annotation for @Finder. Specifies connection type to use in ambiguous cases
 * (when finder can't detect connection using return type).
 *
 * @author Vyacheslav Rusakov
 * @since 02.08.2014
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Use {

    /**
     * @return connection type to use if finder can't detect exact type.
     */
    DbType value() default DbType.DOCUMENT;
}
