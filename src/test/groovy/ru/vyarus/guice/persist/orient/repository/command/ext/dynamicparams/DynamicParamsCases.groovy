package ru.vyarus.guice.persist.orient.repository.command.ext.dynamicparams

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar
import ru.vyarus.guice.persist.orient.repository.command.ext.param.Param
import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * @author Vyacheslav Rusakov 
 * @since 27.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface DynamicParamsCases {

    @Query("select from Model where name=? and nick=?")
    List<ODocument> positionalList(@DynamicParams List<String> params);

    @Query("select from Model where name=? and nick=?")
    List<ODocument> positionalArray(@DynamicParams String[] params);

    @Query("select from Model where name=? and nick=?")
    List<ODocument> positionalVararg(@DynamicParams String... params);

    @Query("select from Model where name=:name and nick=:nick")
    List<ODocument> namedMap(@DynamicParams Map<String, String> params);

    @Query("select from Model where name=? and nick=?")
    List<ODocument> mixPositional(String name, @DynamicParams String... params);

    @Query("select from Model where name=:name and nick=:nick")
    List<ODocument> mixNamed(@Param("name") String name, @DynamicParams Map<String, String> params);

    // example of universal method: any conditions, any params
    @Query('select from Model where ${cond}')
    List<ODocument> universalus(@ElVar("cond") String cond, @DynamicParams Object... params);

    // error: wrong type
    @Query("select from Model where name=? and nick=?")
    List<ODocument> wrongType(@DynamicParams Object params);

    // error: duplicate definition
    @Query("select from Model where name=? and nick=?")
    List<ODocument> duplicate(@DynamicParams Object[] params, @DynamicParams Object[] params2);

    // error: use named params and positional dynamic
    @Query("select from Model where name=:name and nick=:nick")
    List<ODocument> mixNamedWithPos(@Param("name") String name, @DynamicParams String... params);

    // error: mix positional params with dynamic named
    @Query("select from Model where name=? and nick=:nick")
    List<ODocument> mixPosWithNamed(String name, @DynamicParams Map<String, String> params);

}