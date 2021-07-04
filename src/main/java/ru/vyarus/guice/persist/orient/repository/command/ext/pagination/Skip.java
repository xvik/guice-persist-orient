package ru.vyarus.guice.persist.orient.repository.command.ext.pagination;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as query SKIP parameter. Parameter must be number type.
 * Throws error if applied not to select query.
 * <p>
 * NOTE: SKIP part is added at the end of the string, because it's the only way to set it. In most
 * cases this will be ok, but for some queries it may lead to bad query. In this case write SKIP directly
 * in query.
 * <p>
 * For pagination ise it with {@link Limit} annotation
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/Pagination.html">docs</a>
 * @since 06.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(SkipParamExtension.class)
public @interface Skip {
}
