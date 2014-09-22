package ru.vyarus.guice.persist.orient.finder.placeholder;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as query placeholder.
 *
 * @author Vyacheslav Rusakov
 * @since 21.09.2014
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Placeholder {

    /**
     * @return name of target placeholder
     */
    String value();
}
