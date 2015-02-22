package ru.vyarus.guice.persist.orient.repository.core.ext.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.delegate.ext.instance.Repository
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface IncompatibleParam {

    // error: delegate parameter used in query method
    @Query("select from Model")
    List<Model> selectAll(@Repository Class repo)
}