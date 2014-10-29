package ru.vyarus.guice.persist.orient.finder.delegate.mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used for finder delegate implementation method parameter.
 * Pass caller finder instance as parameter. This could be quite handy if finder interfaces share common
 * interface and you can generify it's handling. For example, you may call some finder methods and
 * aggregate results.
 *
 * @author Vyacheslav Rusakov
 * @since 24.10.2014
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface FinderInstance {
}
