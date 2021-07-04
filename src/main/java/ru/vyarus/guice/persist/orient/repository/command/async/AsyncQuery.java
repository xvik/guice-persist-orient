package ru.vyarus.guice.persist.orient.repository.command.async;

import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sql async query repository method extension. Use only for select queries.
 * <p>
 * Listener parameter ({@link com.orientechnologies.orient.core.command.OCommandResultListener} or
 * {@link ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener}) must be
 * annotated with {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen}.
 * Method must be void or return {@link java.util.concurrent.Future} if non blocking query used.
 * <p>
 * Important: query will be asynchronous (executed in separate thread) only for non blocking mode.
 * <p>
 * Uses {@link com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery} for blocking execution and
 * {@link com.orientechnologies.orient.core.sql.query.OSQLNonBlockingQuery} for non blocking.
 * <p>
 * Listener will be wrapped with an external transaction (thread bound listener connection is accessible through
 * guice). For blocking (default) execution, orient calls listener at the same thread as query itself
 * (so transaction is the same).
 * <p>
 * If listener throws an exception during result processing it will be logged, but not propagated. Instead,
 * false will be returned to stop query processing. This behaviour is important to let orient properly handle
 * connection (actually problem is only with non blocking under remote connection, but behaviour is unified
 * to avoid confusion). This behaviour almost unifies live and async listeners behaviour (but in case of live
 * hiding errors is orient native feature).
 * <p>
 * Query could contain variables in format (${var}). By default, only declared type generic names
 * could be used, but extensions could provide other variables (like
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}).
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/java/Document-API-Documents.html#asynchronous-queries">docs</a>
 * @see ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener
 * @see com.orientechnologies.orient.core.command.OCommandResultListener
 * @see ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
 * @since 27.02.2015
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RepositoryMethod(AsyncQueryMethodExtension.class)
public @interface AsyncQuery {

    /**
     * @return query string
     */
    String value();

    /**
     * Blocking execution use {@link com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery}. Listener
     * will be called at the same thread and under query transaction. Useful for filtering results
     * (almost the same like sync query but with ability to stop processing further results). Used by default as
     * less error prone (use non blocking when you really have a reason).
     * <p>
     * Non blocking execution use {@link com.orientechnologies.orient.core.sql.query.OSQLNonBlockingQuery}.
     * With non blocking query, repository method may return {@link java.util.concurrent.Future} (to be able to do
     * {@code result = repository.select(listener}.get()}). Listener will be called in the separate thread.
     * NOTE: current orient implementation does not support feature cancellation
     * (see {@link com.orientechnologies.orient.core.sql.query.OSQLNonBlockingQuery.ONonBlockingQueryFuture}).
     * <p>
     * In essence, non blocking queue is a {@link com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery}
     * launched in separate thread, but with one important difference: database connection is copied from original
     * query connection and bound to listener thread before ALL listener calls and closed after (that is why
     * important to not create new connections there, but use already bound connection).
     * <p>
     * Non blocking query with remote connection will not allow you to perform any query or update operations
     * with thread bound connection. But for blocking or non blocking with local connection thread
     * bound connection could be used as usual. Keep in mind this behaviour difference, Event better, prefer
     * not to use connection inside listener at all (async listener should be fast).
     *
     * @return true to wait for query while listener process results, false to call listener in separate threads
     * @see <a href="https://orientdb.org/docs/3.1.x/java/Document-API-Documents.html#non-blocking-queries">doc</a>
     */
    boolean blocking() default true;
}
