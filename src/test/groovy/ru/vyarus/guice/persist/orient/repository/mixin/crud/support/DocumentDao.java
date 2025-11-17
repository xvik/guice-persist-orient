package ru.vyarus.guice.persist.orient.repository.mixin.crud.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.repository.mixin.support.CustomMixin
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.DocumentCrud
import ru.vyarus.guice.persist.orient.support.repository.mixin.pagination.Pagination
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 26.10.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
public interface DocumentDao extends DocumentCrud<Model>,
        CustomMixin<ODocument, String>,
        Pagination<Model, ODocument> {

}