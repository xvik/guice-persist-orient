package ru.vyarus.guice.persist.orient.repository.core.util.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@ProvidedBy(DynamicSingletonProvider) // generated class
@Delegate(UsualRepository) // will trigger aop proxy (+1 generated class)
@Transactional
interface UsualRepository {

    List<Model> foo();
}
