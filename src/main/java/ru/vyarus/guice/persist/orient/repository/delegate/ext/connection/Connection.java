package ru.vyarus.guice.persist.orient.repository.delegate.ext.connection;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to pass connection object according to repository method return type analysis.
 * <p>
 * Resolved connection will be passed to delegate method. For example, it may be used to
 * avoid writing redundant providers injection and use direct connection from method argument.
 * Also, may be used for object/document mixins: object and document connections share common abstraction
 * ({@link com.orientechnologies.orient.core.db.ODatabaseInternal}).
 * <p>
 * {@link ru.vyarus.guice.persist.orient.repository.delegate.Delegate} annotation support connection hint,
 * which may help selecting correct connection type.
 *
 * @author Vyacheslav Rusakov
 * @since 26.10.2014
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(ConnectionParamExtension.class)
public @interface Connection {
}
