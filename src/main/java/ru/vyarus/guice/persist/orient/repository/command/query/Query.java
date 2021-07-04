package ru.vyarus.guice.persist.orient.repository.command.query;

import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Sql query repository method extension.
 * Use for select/update/insert queries.
 * <p>
 * Uses {@link com.orientechnologies.orient.core.sql.query.OSQLSynchQuery} for queries and
 * {@link com.orientechnologies.orient.core.sql.OCommandSQL} for update and insert commands.
 * <p>
 * Query could contain variables in format (${var}). By default, only declared type generic names
 * could be used, but extensions could provide other variables (like
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}).
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/">docs</a>
 * @since 02.02.2015
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RepositoryMethod(QueryMethodExtension.class)
public @interface Query {

    /**
     * @return query string
     */
    String value();

    /**
     * Use this clause to specify a collection impl to autobox result lists into. The impl must
     * have a default no-arg constructor and be a subclass of {@link java.util.Collection}.
     * @return configured autoboxing collection class.
     */
    Class<? extends Collection> returnAs() default Collection.class;

    /**
     * Connection is automatically detected according to result entity.
     * But in some cases it's impossible. For such rare cases this parameter could help select correct
     * connection.
     *
     * @return connection hint
     */
    DbType connection() default DbType.UNKNOWN;
}
