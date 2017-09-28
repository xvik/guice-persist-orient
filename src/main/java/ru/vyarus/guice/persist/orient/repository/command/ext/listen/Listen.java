package ru.vyarus.guice.persist.orient.repository.command.ext.listen;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Parameter extension to pass custom result listener for command (see
 * {@link com.orientechnologies.orient.core.command.OCommandRequestAbstract#setResultListener(
 *com.orientechnologies.orient.core.command.OCommandResultListener)}.
 * <p>
 *  Listener could be used with {@link ru.vyarus.guice.persist.orient.repository.command.query.Query},
 * {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery} and
 * {@link ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery}.
 * <p>
 * Listener could be applied only for select queries. For sync and async queries method should be void (because listener
 * intercept all results and empty list always returned) and listener type must be
 * {@link com.orientechnologies.orient.core.command.OCommandResultListener}. For live query annotated method
 * must return int (live subscription token required to unsubscribe query) and listener type must be
 * {@link com.orientechnologies.orient.core.sql.query.OLiveResultListener}.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(ListenParamExtension.class)
public @interface Listen {
}
