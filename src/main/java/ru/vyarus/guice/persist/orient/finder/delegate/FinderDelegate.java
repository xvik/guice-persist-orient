package ru.vyarus.guice.persist.orient.finder.delegate;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Delegates finder method execution to bean method.
 * Delegate may be used in root finder class to simply redirect call from interface finder into
 * some bean or may be used in mixin (generic finder interface).
 * <p>Delegate classes are guice beans (it may be already existent daos). It's not necessary to explicitly
 * register this beans (guice may simply create instance with autowiring when requested). Pay attention
 * for bean scope (use singleton for most cases). Bean could possibly be any scope - provider is used
 * to obtain actual instance.</p>
 * <p>On root finder annotation must be declared on method. For generic mixin (interface which your finder will extend)
 * annotation may be defined on type to set global delegation for all methods to one bean.</p>
 * <p>Target method is searched as direct public method of target bean (inherited methods are ignored):</p>
 * <ul>
 * <li>If method name set directly, look only methods with this name (direct method name should be avoided,
 * because it introduce weak contract and not refactor-friendly). If method name not set look all methods.</li>
 * <li>Check all methods for parameter compatibility. Target method must have compatible parameters
 * at the same order! Special parameters (see below) may appear at any position.</li>
 * <li>If more then one method found, use finder method name to reduce results</li>
 * <li>Method with special parameters is prioritized. So if few methods found but only one has additional
 * parameters - it will be chosen.</li>
 * <li>Next methods are filtered by most specific parameters (e.g. two methods with the same name but one declares
 * String parameter and other Object; first one will be chosen as more specific).</li>
 * <li>If we still have more than one possibility, error will be thrown.</li>
 * </ul>
 * <p>Special parameters may be used for delegates. They are useful, when writing generic mixins.</p>
 * <ul>
 * <li>{@link ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderGeneric} allows to pass finder's generic
 * value to target method</li>
 * <li>{@link ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderInstance} allows to pass calling finder itself
 * as parameter</li>
 * <li>{@link ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderDb} used to pass connection object,
 * resolved by finder method return analysis (the same logic detects target connection for sql finder methods).
 * As with sql finder, {@link ru.vyarus.guice.persist.orient.finder.Use} annotation may be used
 * to define exact connection type.</li>
 * </ul>
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface FinderDelegate {

    /**
     * @return delegate class with actual method implementation.
     */
    Class value();

    /**
     * Optional. By default the same method would be checked as finder method name.
     * And even if names are different parameter types could be enough to find appropriate method.
     * If target method couldn't be guessed, this attribute would be last resort.
     *
     * @return target delegate method name
     */
    String method() default "";

    /**
     * Use this clause to specify a collection impl to autobox result lists into. The impl must
     * have a default no-arg constructor and be a subclass of {@code java.util.Collection}.
     * @return the configured autoboxing collection class.
     */
    Class<? extends Collection> returnAs() default Collection.class;
}
