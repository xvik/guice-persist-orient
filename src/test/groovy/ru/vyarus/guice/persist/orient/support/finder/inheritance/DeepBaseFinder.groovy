package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.finder.Finder

/**
 * Finder on 2nd hierarchy level (PowerFinder -> BaseFinder1 -> DeepBaseFinder).
 *
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
public interface DeepBaseFinder<K> {

    @Finder(query = 'select from ${K}')
    K selectOne()

    @Finder(query = 'select from ${K}')
    K[] selectCustom()

    @Finder(query = 'select from ${K}')
    com.google.common.base.Optional<K> selectOptional()

    @Finder(query = 'select from ${K}')
    Iterator<K> selectAllIterator()
}