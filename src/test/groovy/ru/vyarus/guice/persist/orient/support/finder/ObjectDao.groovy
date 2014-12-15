package ru.vyarus.guice.persist.orient.support.finder

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.support.finder.mixin.crud.ObjectCrudMixin
import ru.vyarus.guice.persist.orient.support.finder.mixin.pagination.PaginationMixin
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 26.10.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
interface ObjectDao extends ObjectCrudMixin<Model>,
        CustomMixin<Model, String>,
        PaginationMixin<Model, Model> {
}
