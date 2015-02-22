package ru.vyarus.guice.persist.orient.repository.mixin.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * Repository doesn't define it's own methods, but still has methods from inherited repositories.
 * Generic binds base repository methods to exact type.
 *
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
public interface PowerRepository extends BaseRepository1<Model>, BaseRepository2<Model, ODocument>,
        ComplexGeneric<Model, List<Model>>, ComplexGeneric2<Model, Model[], List<Model>> {

}