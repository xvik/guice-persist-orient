package ru.vyarus.guice.persist.orient.repository.command.ext.dynamicparams;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as dynamic parameters. May be used when positional parameters count vary (e.g. el variable
 * used to add sub query) or when its handy to compose named parameters manually.
 * <p>
 * Type of parameters checked from parameter type: if {@link java.util.List}, array or vararg then positional,
 * if {@link java.util.Map} then named params.
 * <p>
 * If map used, it's keys converted to string, so e.g. enum map could be easily used.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(DynamicParamsExtension.class)
public @interface DynamicParams {
}
