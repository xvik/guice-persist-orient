package ru.vyarus.guice.persist.orient.repository.command.ext.var

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.command.script.Script
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 25.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface VarCases {

    @Script("return \$tst")
    String script(@Var("tst") String var);

    @Query('select name from Model where name = $tst')
    String string(@Var("tst") String tst)

    @Query('select name from Model where name in $tst')
    String[] list(@Var("tst") List tst)

    // error: empty variable
    @Query('select from Model where name = $tst')
    List<Model> empty(@Var(" ") String tst)

    // error: duplicate
    @Query('select from Model where name = $tst')
    List<Model> duplicate(@Var("tst") String tst, @Var("tst") String str)
}