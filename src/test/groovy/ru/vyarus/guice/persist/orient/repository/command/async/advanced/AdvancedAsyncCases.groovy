package ru.vyarus.guice.persist.orient.repository.command.async.advanced

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.model.VersionedEntity
import ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery
import ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.support.model.Model

import java.util.concurrent.Future

/**
 * @author Vyacheslav Rusakov
 * @since 15.10.2017
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface AdvancedAsyncCases {

    // listener will convert document to model
    @AsyncQuery("select from Model")
    void select(@Listen AsyncQueryListener<Model> listener)

    // no conversion will be performed in listener
    @AsyncQuery("select from Model")
    void selectDoc(@Listen AsyncQueryListener<ODocument> listener)

    // listener will project document to single value
    @AsyncQuery("select name from Model")
    void selectProjection(@Listen AsyncQueryListener<String> listener)

    // document will be converted to vertex
    @AsyncQuery("select from VertexModel")
    void selectVertex(@Listen AsyncQueryListener<Vertex> listener)

    // looks like incorrect, but correct type will be resolved from listener (it must be generified with model)
    @AsyncQuery("select from Model")
    void selectPolymorphic(@Listen AsyncQueryListener<VersionedEntity> listener)

    // listener will be called in separate thread and transaction opened for each result
    @AsyncQuery(value = "select from Model", blocking = false)
    Future<List<Model>> selectNonBlock(@Listen AsyncQueryListener<Model> listener)
}