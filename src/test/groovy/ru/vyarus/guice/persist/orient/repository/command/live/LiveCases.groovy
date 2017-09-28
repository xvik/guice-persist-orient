package ru.vyarus.guice.persist.orient.repository.command.live

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.command.OCommandResultListener
import com.orientechnologies.orient.core.sql.query.OLiveResultListener
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud

/**
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface LiveCases extends ObjectCrud<Model> {

    @LiveQuery("select from Model")
    int subscribe(@Listen OLiveResultListener listener)

    // unsubscribe does not support parameters https://github.com/orientechnologies/orientdb/issues/6706
    @Query("live unsubscribe \${token}")
    void unsubscribe(@ElVar("token") int token)

    // error: not String return type
    @LiveQuery("select from Model")
    void notInt(@Listen OLiveResultListener listener)

    // error: no listener
    @LiveQuery("select from Model")
    Integer noListener()

    // error: bad listener type
    @LiveQuery("select from Model")
    int badListener(@Listen OCommandResultListener listener)
}