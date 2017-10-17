package ru.vyarus.guice.persist.orient.repository.command.query.advanced

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.command.OCommandResultListener
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.model.VersionedEntity
import ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov
 * @since 16.10.2017
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface AdvancedQueryCases {

    // listener will convert document to model
    @Query("select from Model")
    void selectObject(@Listen QueryListener<Model> listener)

    // listener will project document to single value
    @Query("select name from Model")
    void selectName(@Listen QueryListener<String> listener)

    // document will be converted to vertex
    @Query("select from VertexModel")
    void selectVertex(@Listen QueryListener<Vertex> listener)

    // orient listener usage
    @Query("select from Model")
    void selectSimple(@Listen OCommandResultListener listener)

    // synthetically incorrect, but test correct type resolution from listener (it must be generified with model)
    @Query("select from Model")
    void selectPolymorphic(@Listen QueryListener<VersionedEntity> listener)

    // error: return type must be void
    @Query("select from Model")
    List<Model> selectNotVoid(@Listen QueryListener<Model> listener)
}