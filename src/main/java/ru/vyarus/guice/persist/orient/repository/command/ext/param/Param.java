package ru.vyarus.guice.persist.orient.repository.command.ext.param;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks query parameter as named.
 * In query named parameters defined as ":name".
 * <p>
 * Query must contain either positional or named parameters.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext
 * @since 06.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(NamedParamExtension.class)
public @interface Param {

    /**
     * @return query named parameter name
     */
    String value();
}
