package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * Finder doesn't define it's own methods, but still has methods from inherited finders.
 * Generic binds base finder methods to exact type.
 *
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
public interface PowerFinder extends BaseFinder1<Model>, BaseFinder2<Model, ODocument>,
        ComplexGeneric<Model, List<Model>>, ComplexGeneric2<Model, Model[], List<Model>> {

}