package ru.vyarus.guice.persist.orient.support.finder

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.support.finder.mixin.crud.DocumentCrudMixin
import ru.vyarus.guice.persist.orient.support.finder.mixin.pagination.PaginationMixin
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 26.10.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
public interface DocumentDao extends DocumentCrudMixin<Model>,
        CustomMixin<ODocument, String>,
        PaginationMixin<Model, ODocument> {

}