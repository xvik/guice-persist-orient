package ru.vyarus.guice.persist.orient.support.finder

import com.google.inject.persist.Transactional
import com.google.inject.persist.finder.Finder
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * Service with finder methods
 *
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@javax.inject.Singleton
class BeanFinder {

    @Transactional
    @Finder(query = "select * from Model")
    List<Model> selectAll() {
        throw new UnsupportedOperationException("Should be handled with finder interceptor");
    }
}
