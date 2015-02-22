package ru.vyarus.guice.persist.orient.repository.command.ext.placeholder;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * It's very dangerous to use raw placeholder substitution because of possible malicious sql injection.
 * To safeguard code, define all possible options for substitution value. This way system will be able to
 * validate placeholder value and deny possible attacks.
 *
 * @author Vyacheslav Rusakov
 * @since 21.09.2014
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface PlaceholderValues {

    /**
     * @return placeholder name
     */
    String name();

    /**
     * @return placeholder possible values
     */
    String[] values();
}

