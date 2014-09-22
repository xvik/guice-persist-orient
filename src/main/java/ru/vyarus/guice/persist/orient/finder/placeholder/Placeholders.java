package ru.vyarus.guice.persist.orient.finder.placeholder;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Group {@code PlaceholderValue} definitions, when more then one placeholder used.
 *
 * @author Vyacheslav Rusakov
 * @since 21.09.2014
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Placeholders {
    PlaceholderValues[] value();
}
