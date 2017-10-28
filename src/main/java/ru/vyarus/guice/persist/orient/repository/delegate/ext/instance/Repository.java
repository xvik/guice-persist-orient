package ru.vyarus.guice.persist.orient.repository.delegate.ext.instance;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used for repository delegate implementation method parameter.
 * Pass caller repository instance as parameter.
 * <p>
 * This could be quite handy if repositories share common interface and you can generify it's handling.
 * <p>
 * For example, you may call some repository methods and aggregate results.
 *
 * @author Vyacheslav Rusakov
 * @since 24.10.2014
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(RepositoryParamExtension.class)
public @interface Repository {
}
