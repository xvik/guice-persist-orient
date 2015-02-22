package ru.vyarus.guice.persist.orient.repository.core.ext.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.CmdAmend
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@ProvidedBy(DynamicSingletonProvider)
interface IllegalDeclaration {

    // error: two extensions declared
    @Delegate(IllegalDeclaration)
    @Query("select from Model")
    List<Model> selectAll();
}
