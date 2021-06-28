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
 * com.orientechnologies.orient.core.command.OCommandResultListener)}.
 * <p>
 *  Listener could be used with {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery} and
 * {@link ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery}.
 * <p>
 * Listener could be applied only for select queries.
 * <p>
 * For async queries method should be void (because listener intercept all results and empty list always returned)
 * or return {@link java.util.concurrent.Future} (for non blocking
 * {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery#blocking()})
 * and listener type must be {@link com.orientechnologies.orient.core.command.OCommandResultListener} or
 * {@link ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener} if type
 * conversions required.
 * <p>
 * For live query, annotated method must return int (live subscription token required to unsubscribe query) and
 * listener type must be raw {@link com.orientechnologies.orient.core.sql.query.OLiveResultListener} or
 * {@link ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener} if type
 * conversions required.
 * <p>
 * Orient requires a connection for listeners execution, so there is always a connection object bound to thread.
 * External transaction ({@link ru.vyarus.guice.persist.orient.db.transaction.TxConfig#external()}) will be
 * started for listeners to let you use connection inside listener (also connection is required for
 * object conversions if custom mapper listener is used). For blocking async listener (default) it will always be
 * executed within query transaction (controlled by guice).
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
