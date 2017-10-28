package ru.vyarus.guice.persist.orient.repository.delegate;

import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Delegates repository method execution to another guice bean method.
 * Delegate may be used in interfaces to redirect call from interface method into
 * some guice bean with implementation.
 * <p>
 * Delegate classes are guice beans (it may be already existent daos). It's not necessary to explicitly
 * register this beans (guice may simply create instance with autowiring when requested). Pay attention
 * for bean scope (use singleton for most cases). Bean could possibly be any scope - provider is used
 * to obtain actual instance.
 * <p>
 * Annotation could be declared directly on method or on type to delegate all method calls.
 * <p>
 * Target method search algorithm:
 * <ul>
 * <li>If method name set directly, look only methods with this name (direct method name should be avoided,
 * because it introduce weak contract and not refactor-friendly). If method name not set look all methods.</li>
 * <li>Check all methods for parameter compatibility. Target method must have compatible parameters
 * at the same order (ignoring parameter extensions)! Parameter extensions may appear at any position.</li>
 * <li>If more then one method found, use called method name to reduce results</li>
 * <li>Method with parameter extensions is prioritized. So if few methods found but only one has extension
 * parameters - it will be chosen.</li>
 * <li>Next methods are filtered by most specific parameters (e.g. two methods with the same name but one declares
 * String parameter and other Object; first one will be chosen as more specific).</li>
 * <li>If we still have more than one possibility, error will be thrown.</li>
 * </ul>
 * <p>
 * Parameter extensions are always add implicit parameter to delegated method call. For example see
 * {@link ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic},
 * {@link ru.vyarus.guice.persist.orient.repository.delegate.ext.instance.Repository},
 * {@link ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.Connection} extensions.
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@RepositoryMethod(DelegateMethodExtension.class)
@Documented
public @interface Delegate {

    /**
     * @return delegate class with actual method implementation.
     */
    Class value();

    /**
     * Optional. By default the same method would be checked as repository method name.
     * And even if names are different parameter types could be enough to find appropriate method.
     * If target method couldn't be guessed, this attribute would be last resort.
     *
     * @return target delegate method name
     */
    String method() default "";

    /**
     * Use this clause to specify a collection impl to autobox result lists into. The impl must
     * have a default no-arg constructor and be a subclass of {@link java.util.Collection}.
     *
     * @return the configured autoboxing collection class.
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
