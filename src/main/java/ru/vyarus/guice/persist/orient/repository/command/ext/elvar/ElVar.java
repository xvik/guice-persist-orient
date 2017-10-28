package ru.vyarus.guice.persist.orient.repository.command.ext.elvar;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as command el variable value (command could contain variables as ${name}).
 * <p>
 * Any object may be used as parameter type (to string will be simply applied).
 * Enum is the most preferred type (most safe one). When {@link java.lang.Class} type specified, it's
 * converted to string by getting simple name (just class name). Classes may be useful as schema types.
 * <p>
 * In case of string parameter, there is a huge possibility of sql injection,
 * so it's better to declare possible variable values. If possible values not declared, warning will be printed
 * in logs. To remove warning message use safe annotation flag, but be sure that variable is secured from injection.
 *
 * @author Vyacheslav Rusakov
 * @since 21.09.2014
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(ElVarParamExtension.class)
public @interface ElVar {

    /**
     * @return name of target el var
     */
    String value();

    /**
     * Use this for string variables to safe from sql injection.
     *
     * @return list of possible values
     */
    String[] allowedValues() default {};

    /**
     * By default, String and Object parameters will cause warning in logs if no allowed values specified.
     * If you sure that parameter is safe you can set true and avoid warning logs (also, it will document safety in
     * source).
     *
     * @return true if parameter is safe and don't requires allowed values list
     */
    boolean safe() default false;
}
