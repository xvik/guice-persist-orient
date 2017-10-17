package ru.vyarus.guice.persist.orient.repository.command.live.advanced

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery
import ru.vyarus.guice.persist.orient.repository.command.live.mapper.LiveQueryListener
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud
import sun.security.provider.certpath.Vertex

/**
 * @author Vyacheslav Rusakov
 * @since 10.10.2017
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface AdvancedLiveCases extends ObjectCrud<Model> {

    @LiveQuery("select from Model")
    int subscribe(@Listen LiveQueryListener<Model> listener)

    @LiveQuery("select from Model where cnt > 1")
    int subscribeConditional(@Listen LiveQueryListener<Model> listener)

    @LiveQuery("select from VertexModel")
    int subscribeVertex(@Listen LiveQueryListener<Vertex> listener)

    // error - conversion will not be possible without enabled transaction (even if listener itself will
    // be @Transactional guice bean
    @LiveQuery("select from Model")
    int subscribeNoTx(@Listen(transactional = false) LiveQueryListener<Model> listener)
}