package ru.vyarus.guice.persist.orient.repository.core.spi.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for repository specific annotations like
 * {@link ru.vyarus.guice.persist.orient.repository.command.query.Query},
 * {@link ru.vyarus.guice.persist.orient.repository.command.function.Function},
 * {@link ru.vyarus.guice.persist.orient.repository.delegate.Delegate}.
 * <p>
 * Aop interceptor is applied for all annotations, annotated with this marker. Annotation may be placed on method
 * or on type (to handle all methods in class). Method annotation is prioritized, so if type and method annotations
 * defined, type annotation will be ignored (this allows defining method annotation for all methods and use more
 * specific annotation just on few of them). But different method extension couldn't be allied to the same method.
 * <p>
 * No specific actions required to register extension.
 *
 * @author Vyacheslav Rusakov
 * @since 02.02.2015
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepositoryMethod {

    /**
     * Extension will be obtained from guice context. Good practice to make such extensions singletons, but
     * other scopes will also work.
     *
     * @return extension type, which should be used for method processing
     */
    Class<? extends RepositoryMethodExtension> value();
}
