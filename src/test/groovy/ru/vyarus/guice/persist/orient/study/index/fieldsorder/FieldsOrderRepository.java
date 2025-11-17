package ru.vyarus.guice.persist.orient.study.index.fieldsorder

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.model.VersionedEntity
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud

/**
 * @author Vyacheslav Rusakov 
 * @since 01.07.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface FieldsOrderRepository extends ObjectCrud<VersionedEntity> {

    @Query("explain select from FOTest where foo=? and bar=?")
    ODocument sameOrder()

    @Query("explain select from FOTest where foo=? and bar=?")
    ODocument reverseOrder()

    @Query("explain select from FOTest where foo=?")
    ODocument foo()

    @Query("explain select from FOTest2 where foo=?")
    ODocument foo2()
}