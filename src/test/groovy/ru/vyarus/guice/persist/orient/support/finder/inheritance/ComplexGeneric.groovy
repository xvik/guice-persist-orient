package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.finder.Finder

/**
 * @author Vyacheslav Rusakov 
 * @since 19.10.2014
 */
public interface ComplexGeneric<T, K extends Collection<T>> {

    @Finder(query = 'select from ${T}')
    K selectAllComplex()
}