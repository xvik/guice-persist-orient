package ru.vyarus.guice.persist.orient.repository.core.ext.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.CmdAmend
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.DelegateAmend
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.UniversalAmend
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
@UniversalAmend // extension could be applied to both methods
@ProvidedBy(DynamicSingletonProvider)
interface Mixin {

    @CmdAmend
    @Query("select from Model")
    List<Model> selectAll();

    @DelegateAmend
    @Delegate(MixinDelegate)
    List<Model> delegate()

    // error: wrong amend extension
    @DelegateAmend
    @Query("select from Model")
    List<Model> selectError();
}
