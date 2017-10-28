package ru.vyarus.guice.persist.orient.repository.core.spi.method;

import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;

import java.lang.annotation.Annotation;

/**
 * Root extension defines repository method behaviour. Extension must be defined with custom annotation,
 * annotated with {@link RepositoryMethod}.
 * <p>
 * Extensions must manually call {@link ru.vyarus.guice.persist.orient.repository.core.ext.SpiService}
 * to support parameter and amend extensions.
 * <p>
 * Extension implementation is obtained from guice context. Any guice scope may be used, but prefer
 * singletons for performance.
 * <p>
 * Extension must be stateless: in case of singleton, the same instance will be used to handle all
 * method calls and with other scopes instances will be different for descriptor creation and execution.
 * Use custom descriptor object for extension specific state.
 *
 * @param <T> descriptor type
 * @param <A> method annotation type
 * @author Vyacheslav Rusakov
 * @since 02.02.2015
 */
public interface RepositoryMethodExtension<T extends RepositoryMethodDescriptor, A extends Annotation> {

    /**
     * Called  to compute extension specific method descriptor. Descriptor is cached, so
     * all reflection and other slow logic used to analyze method will be called one time.
     * Descriptor should contain everything for fast execution.
     * <p>
     * Use {@link ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException} for usage specific
     * errors.
     *
     * @param context    repository method context
     * @param annotation method annotation instance (extension annotation)
     * @return computed descriptor
     */
    T createDescriptor(DescriptorContext context, A annotation);

    /**
     * @param descriptor         computed method descriptor (most likely cached)
     * @param repositoryInstance repository instance object
     * @param arguments          arguments
     * @return method execution result
     * @throws Throwable in case of errors
     */
    Object execute(T descriptor, Object repositoryInstance, Object... arguments) throws Throwable;
}
