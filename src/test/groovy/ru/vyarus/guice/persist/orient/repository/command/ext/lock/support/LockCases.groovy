package ru.vyarus.guice.persist.orient.repository.command.ext.lock.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.storage.OStorage
import ru.vyarus.guice.persist.orient.repository.command.ext.lock.LockStrategy
import ru.vyarus.guice.persist.orient.repository.command.ext.lock.support.ext.CheckLock
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
@CheckLock
interface LockCases {

    @LockStrategy(OStorage.LOCKING_STRATEGY.KEEP_EXCLUSIVE_LOCK)
    @Query("select from Model")
    List<Model> lock()
}