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
 *  Listener could be used with {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery} and
 * {@link ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery}.
 * <p>
 * Listener could be applied only for select queries.
 * <p>
 * For async queries method should be void (because listener intercept all results and empty list always returned)
 * and listener type must be {@link com.orientechnologies.orient.core.command.OCommandResultListener} or
 * {@link ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener} if type
 * conversions required.
 * <p>
 * For live query, annotated method must return int (live subscription token required to unsubscribe query) and
 * listener type must be raw {@link com.orientechnologies.orient.core.sql.query.OLiveResultListener} or
 * {@link ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener} if type
 * conversions required.
 * <p>
 * By default listener execution will be wrapped in transaction (see {@link #transactional()}). If you disable
 * transaction wrapping, listener result conversion (e.g. document to object or vertex) will be impossible.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(ListenParamExtension.class)
public @interface Listen {

    /**
     * Wrap listener with an implicit transaction. This is important if custom listeners (like
     * {@link ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener}) are used,
     * because otherwise they will not be able to convert record to other type.
     * <p>
     * Note that async listener in local connection will be executed synchronously and so as part of the current
     * transaction.
     * <p>
     * Enabled by default because in most cases listener will need to do something with the database and so will
     * need transaction in any way.
     *
     * @return true if listener must be executed in transaction.
     */
    boolean transactional() default true;
}
