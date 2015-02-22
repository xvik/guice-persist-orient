package ru.vyarus.guice.persist.orient.repository.delegate.spi;

import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension;

/**
 * Delegate specific extensions. Used to add special parameters.
 *
 * @param <T> method descriptor type
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
public interface DelegateExtension<T extends DelegateMethodDescriptor> extends AmendExecutionExtension {

    /**
     * Called to fill additional target method arguments. Additional parameters should be inserted directly into
     * targetArgs array. Array already contains direct parameters, so extension could modify even ordinal values
     * if required.
     *
     * @param descriptor repository method descriptor
     * @param targetArgs array of target parameter (will be used to call delegate method)
     * @param instance   repository instance
     * @param arguments  actual repository method arguments
     */
    void amendParameters(T descriptor, Object[] targetArgs, Object instance, Object... arguments);
}
