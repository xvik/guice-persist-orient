package ru.vyarus.guice.persist.orient.repository.command.ext.named

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.ext.param.Param
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface NamedParamsCases {


    // named parameters (using both possible annotations)
    @Query("select from Model where name=:name and nick=:nick")
    List<Model> parametersNamed(@Param("name") String name, @Param("nick") String nick)

    // error because all params must be ordinal or named
    @Query("select from Model where name=? and nick=?")
    List<Model> parametersPositionalWithOrdinal(String name, @Param("nick") String nick)

    // error because all parameters expect to be named, because of first one
    @Query("select from Model where name=:name and nick=:nick")
    List<Model> parametersNames(@Param("name") String name, String nick)

    // error - duplicate parameter name
    @Query("select from Model where name=:name and nick=:nick")
    List<Model> parametersNamesDuplicateName(@Param("name") String name, @Param("name") String nick)
}