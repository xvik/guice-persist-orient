package ru.vyarus.guice.persist.orient.support.finder.subpkg

import com.google.inject.persist.Transactional
import com.google.inject.persist.finder.Finder
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * Finder to check classpath scanner finder module
 *
 * @author Vyacheslav Rusakov 
 * @since 04.08.2014
 */
@Transactional
public interface FoundFinder {

    @Finder(query = "select from Model")
    List<Model> findAll();
}