package ru.vyarus.guice.persist.orient.finder.delegate.mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use for annotating finder delegate method parameter.
 * As with sql finder, connection type is detected based on method return.
 * Resolved connection will be passed to delegate method. For example, it may be used to
 * avoid writing redundant providers injection and use direct connection from method argument.
 * Also, may be used for object/document mixins: object and document connections share common abstraction.
 * As with sql finder {@link ru.vyarus.guice.persist.orient.finder.Use} annotation may be set on finder method
 * to specify exact connection type.
 *
 * @author Vyacheslav Rusakov
 * @since 26.10.2014
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface FinderDb {
}
