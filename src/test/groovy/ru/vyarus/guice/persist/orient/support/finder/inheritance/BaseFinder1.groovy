package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.finder.Finder

/**
 * Sample of generic finder.
 * It can't be used directly but may be used by other finders to provide common functionality.
 * Special placeholder ${T} will be resolved into generic type, provided in interface.
 *
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
public interface BaseFinder1<T> extends DeepBaseFinder<T> {

    @Finder(query = 'select from ${T}')
    List<T> selectAll()
}