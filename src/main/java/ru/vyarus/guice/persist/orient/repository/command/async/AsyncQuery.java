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
 * {@link ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener}) must be annotated
 * with {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen}.
 * Method must be void.
 * <p>
 * Important: query will be asynchronous only for remote connection.
 * <p>
 * Uses {@link com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery}.
 * <p>
 * Query could contain variables in format (${var}). By default, only declared type generic names
 * could be used, but extensions could provide other variables (like
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}).</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://orientdb.com/docs/last/Document-Database.html">docs</a>
 * @see ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener
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
}
