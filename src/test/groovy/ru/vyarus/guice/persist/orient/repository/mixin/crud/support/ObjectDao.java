package ru.vyarus.guice.persist.orient.repository.mixin.crud.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.mixin.support.CustomMixin
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud
import ru.vyarus.guice.persist.orient.support.repository.mixin.pagination.Pagination
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 26.10.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
interface ObjectDao extends ObjectCrud<Model>,
        CustomMixin<Model, String>,
        Pagination<Model, Model> {

    @Query("select from Model where name=?")
    Model findByName(String name);
}
