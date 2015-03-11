package ru.vyarus.guice.persist.orient.repository.command.async;

import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sql async query repository method extension. Use only for select queries.
 * Must be used together with {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen}.
 * Method must be void.
 * <p>Important: query will be asynchronous only for remote connection.</p>
 * <p>Uses {@link com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery}.</p>
 * <p>Query could contain variables in format (${var}). By default, only declared type generic names
 * could be used, but extensions could provide other variables (like
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}).</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/Document-Database.html#asynchronous-query">
 *     docs</a>
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
