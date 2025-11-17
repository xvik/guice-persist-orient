package ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.command.OCommandContext
import ru.vyarus.guice.persist.orient.repository.command.ext.timeout.Timeout
import ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support.ext.TimeoutCheck
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
@TimeoutCheck
interface TimeoutCases {

    @Timeout(200l)
    @Query("select from Model LIMIT 10")
    List<Model> all()

    // too short to execute, custom strategy will return empty list
    @Timeout(value = 1l, strategy = OCommandContext.TIMEOUT_STRATEGY.RETURN)
    @Query("select from Model")
    List<Model> neverDone()

    // not applied timeout
    @Timeout(0l)
    @Query("select from Model LIMIT 10")
    List<Model> noTimeout()

    // error: bad timeout
    @Timeout(-10l)
    @Query("select from Model")
    List<Model> badTimeout()
}