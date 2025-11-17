package ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.support

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
@ProvidedBy(DynamicSingletonProvider)
@Delegate(ConnectionDelegate)
interface ConnectionRepository {

    List<Model> rawConnection()

    List<Model> subtypeMatch()

    List<Model> exactConnection()

    List<Model> incompatible()

    List<Model> duplicate()
}