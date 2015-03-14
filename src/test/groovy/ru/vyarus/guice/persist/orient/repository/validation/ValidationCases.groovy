package ru.vyarus.guice.persist.orient.repository.validation

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * @author Vyacheslav Rusakov 
 * @since 14.03.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface ValidationCases {


    @Query("select from Model where name = ?")
    @Size(min = 1)
    List<Model> select(@NotNull String name)
}
