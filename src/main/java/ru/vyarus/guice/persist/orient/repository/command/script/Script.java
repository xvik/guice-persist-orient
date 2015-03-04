package ru.vyarus.guice.persist.orient.repository.command.script;

import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Script call repository method extension. The most common script types are sql, gremlin, javascript.
 * SQL is the default. Gremlin requires gremlin-groovy dependency, excluded by default.
 * Javascript requires special server configuration (if remote connection used). Other jvm scripting languages
 * could be used.
 * <p>Uses {@link com.orientechnologies.orient.core.command.script.OCommandScript}.</p>
 * <p>Script could contain el variables (${var}). By default, only declared type generic names could be used,
 * but extensions could provide other variables (like
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}).</p>
 * <p>By default, sql function expected, but if other language required set it explicitly.</p>
 * <p>Scripts are very similar to {@link ru.vyarus.guice.persist.orient.repository.command.function.Function}</p>
 * <p>WARNING: script did not support positional parameters (?)! Use named parameters or reference positional
 * parameters by name (e.g. ':0' for first parameter instead of '?')</p>
 * <p>Note that using script instead of update query, like this "begin update... commit" solves
 * concurrent modification problem, even without retry.</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-batch.html">docs</a>
 * @since 25.02.2015
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RepositoryMethod(ScriptMethodExtension.class)
public @interface Script {

    /**
     * @return script to execute
     */
    String value();

    /**
     * @return script language (any scripting language installed in the JVM)
     */
    String language() default "sql";

    /**
     * Use this clause to specify a collection impl to autobox result lists into. The impl must
     * have a default no-arg constructor and be a subclass of {@code java.util.Collection}.
     *
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
    DbType connection() default DbType.DOCUMENT;
}
