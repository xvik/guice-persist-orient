package ru.vyarus.guice.persist.orient.repository.core.spi.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for method parameter extensions (e.g.
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.pagination.Skip},
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.pagination.Limit}.
 * {@link ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic}).
 * <p>
 * Single method parameter could use just one parameter annotation.
 *
 * @author Vyacheslav Rusakov
 * @since 03.02.2015
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodParam {

    /**
     * Extension will be obtained from guice context. Good practice to make such extensions singletons, but
     * other scopes will also work.
     *
     * @return extension type
     */
    Class<? extends MethodParamExtension> value();
}
