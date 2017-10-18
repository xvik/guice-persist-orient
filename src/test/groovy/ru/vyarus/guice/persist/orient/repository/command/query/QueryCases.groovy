package ru.vyarus.guice.persist.orient.repository.command.query

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.command.OCommandResultListener
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach.DetachResult
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 11.03.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface QueryCases {

    @Query("select from Model")
    List<Model> select()

    @Query("update Model set name = ? where name = ?")
    int update(String to, String from)

    @Query("insert into Model (name) values (?)")
    @DetachResult
    Model insert(String name)

    // ERROR: listener can't be used with query
    // in fact, it could work with memory and local connections, but listener is ignored by remote connection
    // so the ability to use listener was completely prohibited
    @Query("select from Model")
    void async(@Listen OCommandResultListener listener)
}