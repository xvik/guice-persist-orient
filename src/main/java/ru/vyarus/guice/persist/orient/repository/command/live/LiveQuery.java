package ru.vyarus.guice.persist.orient.repository.command.live;

import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Live query repository method extension (only for select queries). Query may not start with "live" keyword - it will
 * be appended automatically.
 * <p>
 * Listener parameter ({@link com.orientechnologies.orient.core.sql.query.OLiveResultListener}) must be annotated
 * with {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen}.
 * Method must return int or Integer (subscription token required to unsubscribe).
 * <p>
 * {@link ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener} could be used as
 * listener in order to apply automatic conversions (like in usual repository methods). For example:
 * {@code @LiveQuery("select from Model") int subscribe(@Listen LiveQueryListener<Model> listener)}.
 * <p>
 * Listener will be wrapped with an external transaction (thread bound listener connection is accessible through
 * guice).
 * <p>
 * Uses {@link com.orientechnologies.orient.core.sql.query.OLiveQuery} for execution.
 * <p>
 * Query could contain variables in format (${var}). By default, only declared type generic names
 * could be used, but extensions could provide other variables (like
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}).
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/java/Live-Query.html">docs</a>
 * @see ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
 * @see com.orientechnologies.orient.core.sql.query.OLiveResultListener
 * @see ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener
 * @since 29.09.2017
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RepositoryMethod(LiveQueryMethodExtension.class)
public @interface LiveQuery {

    /**
     * Query may not start with "live" keyword (it will be appended automatically).
     *
     * @return query string
     */
    String value();
}
