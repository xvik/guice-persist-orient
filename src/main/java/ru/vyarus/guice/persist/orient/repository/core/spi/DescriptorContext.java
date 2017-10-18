package ru.vyarus.guice.persist.orient.repository.core.spi;

import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethodExtension;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Repository method context for actual descriptor computation.
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class DescriptorContext {

    /**
     * Root repository type (method declaring type in simple cases and repository root type in hierarchy case).
     */
    public Class<?> type;

    /**
     * Repository method.
     */
    public Method method;

    /**
     * Computed generics context from repository root, set to declaring class type.
     */
    public GenericsContext generics;

    /**
     * Method extension annotation type. May be used for simplified context detection
     * (instead of exact extension type).
     */
    public Class<? extends Annotation> extensionAnnotation;

    /**
     * Method extension type (may be used by amend/param extensions to guide it's behaviour).
     * Set by {@link ru.vyarus.guice.persist.orient.repository.core.ext.SpiService}
     */
    public Class<? extends RepositoryMethodExtension> extensionType;
}
