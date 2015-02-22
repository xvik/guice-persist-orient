package ru.vyarus.guice.persist.orient.repository.delegate.ext.repository.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@Transactional
@Delegate(RepositoryDelegate)
@ProvidedBy(DynamicSingletonProvider)
interface RepositoryRoot extends CustomMixin {

    List<Model> repo()

    List<Model> repoCustom()

    List<Model> badType()

    List<Model> duplicate()
}
