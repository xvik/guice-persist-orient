package ru.vyarus.guice.persist.orient.finder.internal.delegate;

import ru.vyarus.guice.persist.orient.finder.internal.FinderDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.delegate.method.MethodDescriptor;

import javax.inject.Provider;

/**
 * Finder delegate descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"})
public class FinderDelegateDescriptor extends FinderDescriptor {

    public MethodDescriptor method;
    public Provider<Object> instanceProvider;
}
