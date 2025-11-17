package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.support;

import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;

import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 23.02.2015
 */
@Delegate(GenericMixinDelegate.class)
public interface GenericMixin<T> {

    List<T> getAll();

    List<T> getAll2();

    List<T> duplicateGeneric();

    List<T> lookupError();

    List<T> genericError();

    List<T> genericTypeError();
}
