package ru.vyarus.guice.persist.orient.repository.core.spi.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Result conversion extension. Use it to extend default converter
 * {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter}
 * or replace it completely for some methods.
 * <p>
 * Extension must use custom annotation, annotated with this one.
 * <p>
 * Note that simple conversion cases could be solved using just guice aop. These extensions should be used,
 * when default converter must be disabled or additional conversion must be parametrized (from annotation).
 * Also extension may be useful when descriptor result type analysis object could be useful
 * (to avoid manual reflection).
 * <p>
 * Method may have only one converter extension.
 * <p>
 * Annotation could be defined on method or type to apply to all methods. Annotation on method overrides
 * declared type or repository type annotation.
 *
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultConverter {

    /**
     * Extension instance is obtained from guice context. Prefer using singleton scope for performance.
     * Other scopes will also work properly.
     *
     * @return result converter extension type.
     */
    Class<? extends ResultExtension> value();

    /**
     * Default converter
     * {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter} is
     * responsible for common conversion cases. In most cases result extension should be applied after it
     * (for example, to detach objects). But if completely custom conversion behaviour required it may be switched off.
     * <p>
     * By default, default converter is applied.
     *
     * @return true to apply default converter, false to ignore default converter
     */
    boolean applyDefaultConverter() default true;
}
