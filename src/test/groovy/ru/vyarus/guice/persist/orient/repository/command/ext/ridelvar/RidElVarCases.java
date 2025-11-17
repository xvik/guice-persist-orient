package ru.vyarus.guice.persist.orient.repository.command.ext.ridelvar

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.id.ORID
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach.DetachResult
import ru.vyarus.guice.persist.orient.support.model.VertexModel

/**
 * @author Vyacheslav Rusakov 
 * @since 02.06.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface RidElVarCases {

    @Query('select from (traverse out from ?)')
    List<VertexModel> paramPositional(String id)

    @Query('select from (traverse out from ${id})')
    @DetachResult
    List<VertexModel> string(@RidElVar("id") String id)

    @Query('select from (traverse out from ${id})')
    List<VertexModel> orid(@RidElVar("id") ORID id)

    @Query('select from (traverse out from ${id})')
    List<VertexModel> object(@RidElVar("id") VertexModel id)

    @Query('select from (traverse out from ${id})')
    List<VertexModel> document(@RidElVar("id") ODocument id)

    @Query('select from (traverse out from ${id})')
    List<VertexModel> vertex(@RidElVar("id") Vertex id)

    @Query('select from (traverse out from ${id})')
    List<VertexModel> universal(@RidElVar("id") Object id)


    @Query('select from (traverse out from ${ids})')
    List<VertexModel> selectList(@RidElVar("ids") List<VertexModel> ids)

    @Query('select from (traverse out from ${ids})')
    List<VertexModel> selectIterable(@RidElVar("ids") Iterable<VertexModel> ids)

    @Query('select from (traverse out from ${ids})')
    List<VertexModel> selectIterator(@RidElVar("ids") Iterator<VertexModel> ids)

    @Query('select from (traverse out from ${ids})')
    List<VertexModel> selectVararg(@RidElVar("ids") VertexModel... ids)
}