package ru.vyarus.guice.persist.orient.repository.command.async.advanced

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery
import ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov
 * @since 15.10.2017
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface AdvancedAsyncCases {

    @AsyncQuery("select from Model")
    void select(@Listen QueryListener<Model> listener)

    @AsyncQuery("select from Model")
    void selectDoc(@Listen QueryListener<ODocument> listener)

    @AsyncQuery("select name from Model")
    void selectProjection(@Listen QueryListener<String> listener)
}