package ru.vyarus.guice.persist.orient.repository.mixin.graph.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.EdgesSupport
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.ObjectVertexCrud

/**
 * @author Vyacheslav Rusakov 
 * @since 23.06.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface EdgesDao extends ObjectVertexCrud<VertexModel>, EdgesSupport {
}