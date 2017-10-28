package ru.vyarus.guice.persist.orient.repository.core.spi.amend;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for amend method extension annotations. Amend annotations used, when extension is
 * not driven by parameter. For example, timeout behavior could be implemented this way (or queries logging).
 * <p>
 * Annotation could be defined on method or type to apply to all methods. Annotation on method overrides
 * declared type or repository type annotation.
 *
 * @author Vyacheslav Rusakov
 * @since 05.02.2015
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AmendMethod {

    /**
     * Extension will be obtained from guice context. Good practice to make such extensions singletons, but
     * other scopes will also work.
     *
     * @return extension type
     */
    Class<? extends AmendMethodExtension> value();
}
