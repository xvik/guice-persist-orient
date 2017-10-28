package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used for delegate implementation method parameter.
 * Used for generic mixins to get actual generic type of caller declaring interface.
 * <pre>
 * {@code
 * public void someMethod(@Generic("T") Class type)
 * }
 * </pre>
 * This is required, because implementation itself is not generified, but generic is available when repository,
 * for example, implements mixin interface.
 * <p>
 * By default, delegate method declared interface used, but if other type's generic required declare it.
 *
 * @author Vyacheslav Rusakov
 * @since 23.10.2014
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(GenericParamExtension.class)
public @interface Generic {

    /**
     * @return repository generic parameter name
     */
    String value();

    /**
     * By default, delegate method declaring type is used to resolve generics.
     * If this is not desired behaviour, then specify exact type to get generic from.
     * Note this type must be present in caller repository class hierarchy.
     *
     * @return type to resolve generic on.
     */
    Class<?> genericHolder() default Object.class;
}
