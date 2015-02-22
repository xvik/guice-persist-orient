package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.support

import ru.vyarus.guice.persist.orient.repository.delegate.Delegate

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@Delegate(GenericMixinDelegate)
interface GenericMixin<T> {

    List<T> getAll()

    List<T> getAll2()

    List<T> duplicateGeneric()

    List<T> lookupError()

    List<T> genericError()

    List<T> genericTypeError()

}