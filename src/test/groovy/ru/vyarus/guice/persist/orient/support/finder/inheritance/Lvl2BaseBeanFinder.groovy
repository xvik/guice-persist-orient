package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.finder.Finder

/**
 * @author Vyacheslav Rusakov 
 * @since 18.10.2014
 */
class Lvl2BaseBeanFinder<T> {

    @Finder(query = 'select from ${T}')
    public T selectOne() {
        throw new UnsupportedOperationException()
    }

    @Finder(query = 'select from ${T}')
    public List<T> selectAll() {
        throw new UnsupportedOperationException()
    }

    @Finder(query = 'select from ${T}')
    com.google.common.base.Optional<T> selectOptional() {
        throw new UnsupportedOperationException()
    }

    @Finder(query = 'select from ${T}')
    Iterator<T> selectAllIterator() {
        throw new UnsupportedOperationException()
    }
}
