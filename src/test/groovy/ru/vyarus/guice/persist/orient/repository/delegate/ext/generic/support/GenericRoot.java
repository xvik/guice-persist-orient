package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface GenericRoot extends GenericMixin<Model>, OtherMixin<Model> {

}