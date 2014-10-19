package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.finder.Finder
import ru.vyarus.guice.persist.orient.finder.scanner.InvisibleForScanner
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 17.10.2014
 */
@InvisibleForScanner
public interface InvisibleFinder {

    @Finder(query = 'select from Model')
    List<Model> selectAll()

}