package ru.vyarus.guice.persist.orient.repository.command.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface CustomResultCases {

    // returns result wrapped in document
    @Query("select count(@rid) from Model")
    ODocument getCount()

    // returns names wrapped in documents
    @Query("select name from Model")
    List<ODocument> getNames()

    // returns names wrapped in documents
    @Query("select name from Model")
    ODocument[] getNamesArray()
}