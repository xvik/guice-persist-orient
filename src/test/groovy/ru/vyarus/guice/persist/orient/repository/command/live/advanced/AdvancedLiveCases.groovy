package ru.vyarus.guice.persist.orient.repository.command.live.advanced

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.sql.query.OLiveResultListener
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery
import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener
import ru.vyarus.guice.persist.orient.repository.command.query.Query
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

    @Query("live unsubscribe \${token}")
    void unsubscribe(@ElVar("token") int token)

    @LiveQuery("select from Model where cnt > 1")
    int subscribeConditional(@Listen LiveQueryListener<Model> listener)

    @LiveQuery("select from VertexModel")
    int subscribeVertex(@Listen LiveQueryListener<Vertex> listener)

    // check the simplest listener wrapping correctness
    @LiveQuery("select from Model")
    int subscribeDoc(@Listen OLiveResultListener listener)
}