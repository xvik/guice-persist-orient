package ru.vyarus.guice.persist.orient.repository.mixin.graph.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.ext.ridelvar.RidElVar
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.ObjectVertexCrud

/**
 * @author Vyacheslav Rusakov 
 * @since 22.06.2015
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
interface ObjectVertexDao extends ObjectVertexCrud<VertexModel> {

    @Query("select from VertexModel where name=?")
    VertexModel findByName(String name)

    @Query('create edge E from ${from} to ${to}')
    void createEdge(@RidElVar("from") VertexModel from, @RidElVar("to") VertexModel to);

    @Query("select count(@rid) from E")
    long countEdges()
}
