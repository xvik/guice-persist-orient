package ru.vyarus.guice.persist.orient.repository.command.ext.pagination;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as query limit value. Parameter must be number type.
 * <p>For pagination ise it with {@link Skip} annotation</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/Pagination.html">docs</a>
 * @since 06.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(LimitParamExtension.class)
public @interface Limit {
}
