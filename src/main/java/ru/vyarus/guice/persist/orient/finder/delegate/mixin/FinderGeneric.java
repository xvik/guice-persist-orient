package ru.vyarus.guice.persist.orient.finder.delegate.mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used for finder delegate implementation method parameter.
 * Used for generic mixins to get actual generic type of caller finder.
 * <pre>
 * {@code
 * public void someMethod(@FinderGeneric("T") Class type)
 * }
 * </pre>
 * This is required, because implementation itself is not generified, but generic finder gets generic
 * when root finder extends it.
 *
 * @author Vyacheslav Rusakov
 * @since 23.10.2014
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface FinderGeneric {

    /**
     * @return finder generic parameter name
     */
    String value();
}
