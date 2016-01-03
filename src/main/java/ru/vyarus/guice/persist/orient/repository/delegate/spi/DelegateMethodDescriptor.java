package ru.vyarus.guice.persist.orient.repository.delegate.spi;

import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.delegate.param.DelegateParamsDescriptor;

import javax.inject.Provider;
import java.lang.reflect.Method;

/**
 * Delegate repository method descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class DelegateMethodDescriptor extends RepositoryMethodDescriptor<DelegateExtension> {

    /**
     * Target bean type.
     */
    public Class target;

    /**
     * Selected target method for delegation.
     */
    public Method method;

    /**
     * Target bean instance provider (provider used to properly support scopes).
     */
    public Provider<?> instanceProvider;

    /**
     * Delegate method parameters descriptor.
     */
    public DelegateParamsDescriptor params;
}
