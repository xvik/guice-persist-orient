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
 * Uses {@link com.orientechnologies.orient.core.sql.query.OLiveQuery}.
 * <p>
 * Query could contain variables in format (${var}). By default, only declared type generic names
 * could be used, but extensions could provide other variables (like
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}).
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://orientdb.com/docs/last/Live-Query.html">docs</a>
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
