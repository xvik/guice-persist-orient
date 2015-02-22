package ru.vyarus.guice.persist.orient.repository.command.ext.pagination

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface PaginationCases {


    // with page definition
    @Query("select from Model where name=? and nick=?")
    List<Model> parametersPaged(String name, String nick, @Skip int start, @Limit int max);

    // with page definition (as objects)
    @Query("select from Model where name=? and nick=?")
    List<Model> parametersPagedObject(String name, String nick, @Skip Long start, @Limit Long max);

    // error - not allowed duplicate definition
    @Query("select from Model where name=? and nick=?")
    List<Model> parametersPagedDouble(String name, String nick, @Skip int start, @Skip int max);

    // error - page definition must be numbers
    @Query("select from Model where name=? and nick=?")
    List<Model> parametersPagedWrongType(String name, String nick, @Skip String start, @Limit int max);

    // error - page definition must be numbers
    @Query("select from Model where name=? and nick=?")
    List<Model> parametersPagedWrongType2(String name, String nick, @Skip int start, @Limit String max);
}