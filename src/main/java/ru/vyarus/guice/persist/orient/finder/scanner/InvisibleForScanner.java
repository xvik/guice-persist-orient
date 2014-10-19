package ru.vyarus.guice.persist.orient.finder.scanner;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Hides annotated finder interface from classpath scanning.
 *
 * @author Vyacheslav Rusakov
 * @since 17.10.2014
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface InvisibleForScanner {
}
