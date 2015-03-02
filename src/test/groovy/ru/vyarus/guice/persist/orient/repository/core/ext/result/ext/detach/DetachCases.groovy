package ru.vyarus.guice.persist.orient.repository.core.ext.result.ext.detach

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach.DetachResult
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface DetachCases {

    // to validate behaviour without detach
    @Query("select from Model")
    List<Model> select()

    @Query("select from Model")
    @DetachResult
    List<Model> selectDetach()

    @Query("select from Model limit 1")
    @DetachResult
    Model selectPlainDetach()

    @Query("select from Model")
    @DetachResult
    Model[] selectArrayDetach()

    @Query("select from Model")
    @DetachResult
    Set<Model> selectSetDetach()

    @Query("select from Model")
    @DetachResult
    Iterable<Model> selectIterableDetach()

    @Query("select from Model")
    @DetachResult
    Iterator<Model> selectIteratorDetach()

    @Query(value = "select from Model", returnAs = LinkedList)
    @DetachResult
    Collection<Model> selectCustomCollectionDetach()

    // it should be error, but as no checks implemented neither on orient nor on extension side this pass ok
    @Query(value = "select name from Model limit 1", connection = DbType.OBJECT)
    @DetachResult
    String noActualDetach()

    // error: can't detach under document connection
    @Query("select from Model")
    @DetachResult
    Set<ODocument> detachError()
}
