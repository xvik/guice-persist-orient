package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Parameter extension annotation, used to specify fetch plan
 * ({@link com.orientechnologies.orient.core.command.OCommandRequest#setFetchPlan(java.lang.String)}).
 * May have default value to use when no fetch plan provided in parameter.
 * <p>
 * May be applied for String parameters only.
 * <p>
 * If parameter value is empty (null) and no default value specified, fetch plan is not set at all.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/java/Fetching-Strategies.html">docs</a>
 * @since 23.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(FetchPlanParamExtension.class)
public @interface FetchPlan {

    /**
     * Optionally, default fetch plan to use when parameter value is empty (null).
     *
     * @return default fetch plan value
     */
    String value() default "";
}
