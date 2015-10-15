package ru.vyarus.guice.persist.orient.db.retry.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.db.retry.Retry
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.command.script.Script

/**
 * @author Vyacheslav Rusakov
 * @since 03.03.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface DangerUpdatesBean {

    @Query("update model set name=?")
    void update(String name)

    @Retry(100)
    @Query("update model set name=?")
    void updateWithRetry(String name)

    @Script("""
        begin
        update model set name=?
        commit retry 100
        """)
    void updateWithScript(String name)

    @Retry(0)
    @Query("update model set name=?")
    void badRetry(String name)
}
