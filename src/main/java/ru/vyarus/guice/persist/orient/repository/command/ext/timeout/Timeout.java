package ru.vyarus.guice.persist.orient.repository.command.ext.timeout;

import com.orientechnologies.orient.core.command.OCommandContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Timeout amend extension annotation. Used to set command timeout and optionally timeout strategy
 * (see {@link com.orientechnologies.orient.core.command.OCommandRequest#setTimeout(long,
 * com.orientechnologies.orient.core.command.OCommandContext.TIMEOUT_STRATEGY)}).
 * <p>
 * Could be applied directly on method or on type (to apply for all methods).
 * If applied in both places, method annotation used).
 *
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@Documented
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@AmendMethod(TimeoutAmendExtension.class)
public @interface Timeout {

    /**
     * @return timeout in milliseconds (0 to not set timeout)
     */
    long value();

    /**
     * @return timeout strategy to use (default exception)
     */
    OCommandContext.TIMEOUT_STRATEGY strategy() default OCommandContext.TIMEOUT_STRATEGY.EXCEPTION;
}
