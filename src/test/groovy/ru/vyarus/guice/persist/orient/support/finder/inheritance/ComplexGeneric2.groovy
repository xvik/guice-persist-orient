package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.finder.Finder

/**
 * Check case when type specified as array and when type specified as list.
 *
 * @author Vyacheslav Rusakov 
 * @since 19.10.2014
 */
public interface ComplexGeneric2<I, T, K> {

    @Finder(query = 'select from ${I}')
    T selectCustomArray()

    @Finder(query = 'select from ${I}')
    K selectCustomList()
}