package ru.vyarus.guice.persist.orient.repository.command.ext.placeholder;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as query placeholder. Enum and String parameters could be used.
 * <p>In case of string parameter, there is a huge possibility of sql injection,
 * so you need to use {@link ru.vyarus.guice.persist.orient.repository.command.ext.placeholder.PlaceholderValues}
 * to declare possible placeholder values.</p>
 * <p>If multiply placeholders used, use
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.placeholder.Placeholders} to group annotations.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 21.09.2014
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(PlaceholderParamExtension.class)
public @interface Placeholder {

    /**
     * @return name of target placeholder
     */
    String value();
}
