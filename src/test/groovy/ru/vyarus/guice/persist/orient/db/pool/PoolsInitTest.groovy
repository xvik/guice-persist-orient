package ru.vyarus.guice.persist.orient.db.pool

import com.google.inject.Inject
import com.google.inject.persist.PersistService
import ru.vyarus.guice.persist.orient.db.pool.support.MockPoolsModule
import ru.vyarus.guice.persist.orient.db.pool.support.pool.MockDocumentPool
import ru.vyarus.guice.persist.orient.db.pool.support.pool.MockObjectPool
import ru.vyarus.guice.persist.orient.db.DatabaseManager
import ru.vyarus.guice.persist.orient.db.DbType
import spock.guice.UseModules
import spock.lang.Specification


/**
 * Check pools correctly init and stop by persist service
 *
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
@UseModules(MockPoolsModule)
class PoolsInitTest extends Specification {

    @Inject
    PersistService persist
    @Inject
    MockDocumentPool documentPool
    @Inject
    MockObjectPool objectPool

    def "Check pools lifecycle"() {

        when: "starting db"
        persist.start()
        then: "pools initialized"
        documentPool.started
        objectPool.started

        when: "duplicate start check"
        persist.start()
        then: "just warn in logs"
        documentPool.started
        objectPool.started

        when: "shutdown db"
        persist.stop()
        then: "pools closed"
        !documentPool.started
        !objectPool.started

        when: "double shutdown ok"
        persist.stop()
        then: "pools closed"
        !documentPool.started
        !objectPool.started
    }

    def "Check types recording"() {

        when: "starting pools"
        persist.start()
        then: "two types available"
        ((DatabaseManager) persist).getSupportedTypes() == [DbType.DOCUMENT, DbType.OBJECT] as Set
    }
}