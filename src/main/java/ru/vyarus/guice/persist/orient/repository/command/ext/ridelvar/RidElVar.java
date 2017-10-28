package ru.vyarus.guice.persist.orient.repository.command.ext.ridelvar;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as rid el variable. {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}
 * is a generic mechanism, whereas {@link RidElVar} exist specifically for binding
 * id's into into query from various possible sources.
 * <p>
 * The main reason why this extension exist is current orient sql parser inconsistencies. For example,
 * sub queries not support parameters {@code "select from (traverse out from ?)"} (neither positional nor named
 * parameters will work). There are other situations when parameters doesn't work. Situation will get better
 * over time, and until then extension will be a "silver bullet" for such cases.
 * <p>
 * The most simple solution is to inject rids directly (as it's shown in documentation) into query string
 * with el var.
 * <p>
 * Parameter could be ODocument, Vertex, object (proxy or raw object), string rid, ORID,
 * collection (Iterable, Iterator) or array. In case of single object rid will be resolved and injected (as #N:N).
 * In case of collection, "[rid1, rid2, etc]" will be injected.
 * <p>
 * Null rids are not accepted (exception casted). If string rid used, it's checked before injecting in query
 * to shield from sql injection. Not saved object proxy, document, vertex will contain fake id and will be accepted
 * (but query result will obviously be incorrect).
 * <p>
 * Example usage:
 * <pre>{@code
 *  &copy;Query("select from (traverse out from ${id})")
 *  public List doSmth(@RidElVar("id") String id)
 * }</pre>
 * Or
 * <pre>{@code
 *  &copy;Query("select from (traverse out from ${id})")
 *  public List doSmth(@RidElVar("id") ODocument id)
 * }</pre>
 * Or even raw Object to accept all cases (universal)
 * <pre>{@code
 *  &copy;Query("select from (traverse out from ${id})")
 *  public List doSmth(@RidElVar("id") Object id)
 * }</pre>
 * Or collection
 * <pre>{@code
 *  &copy;Query("select from (traverse out from ${ids})")
 *  public List doSmth(@RidElVar("ids") List<ODocument> ids)
 * }</pre>
 * Or varag (array)
 * <pre>{@code
 *  &copy;Query("select from (traverse out from ${ids})")
 *  public List doSmth(@RidElVar("ids") ODocument... ids)
 * }</pre>
 * @author Vyacheslav Rusakov
 * @since 02.06.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(RidElVarParamExtension.class)
public @interface RidElVar {

    /**
     * @return variable name
     */
    String value();
}
