package ru.vyarus.guice.persist.orient.repository.delegate.support.amend

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface AmendedDelegate {

    @Query("select from Model")
    List<Model> select()
}
