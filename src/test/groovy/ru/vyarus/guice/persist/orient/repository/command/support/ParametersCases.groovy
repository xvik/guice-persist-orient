package ru.vyarus.guice.persist.orient.repository.command.support

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
interface ParametersCases {

    // positional parameters
    @Query("select from Model where name=? and nick=?")
    List<Model> parametersPositional(String name, String nick)

    // just check vararg (it will not work)
    @Query("select from Model where name in ?")
    List<Model> findWithVararg(String... names);

    // just check list
    @Query("select from Model where name in ?")
    List<Model> findWithList(List<String> names);

    // using parameters in update query
    @Query("update Model set name=? where name=?")
    int updateWithParam(String toName, String whereName);
}