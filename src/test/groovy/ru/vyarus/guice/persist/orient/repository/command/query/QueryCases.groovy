package ru.vyarus.guice.persist.orient.repository.command.query

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
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
}