package ru.vyarus.guice.persist.orient.repository.command.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface OptionalCases {

    // use guava optional
    @Query("select from Model")
    com.google.common.base.Optional<Model> findGuavaOptional();

    // check empty collection result conversion to single element
    @Query("select from Model where name='not existent'")
    com.google.common.base.Optional<Model> emptyCollection();
}