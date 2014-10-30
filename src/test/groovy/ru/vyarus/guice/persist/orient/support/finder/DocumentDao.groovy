package ru.vyarus.guice.persist.orient.support.finder

import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.support.finder.mixin.crud.DocumentCrudMixin

/**
 * @author Vyacheslav Rusakov 
 * @since 26.10.2014
 */
@Transactional
public interface DocumentDao extends DocumentCrudMixin, CustomMixin<ODocument, String> {

}