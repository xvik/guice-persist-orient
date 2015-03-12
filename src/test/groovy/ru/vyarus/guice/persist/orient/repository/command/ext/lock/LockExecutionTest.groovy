package ru.vyarus.guice.persist.orient.repository.command.ext.lock

import com.google.inject.Inject
import com.orientechnologies.orient.core.storage.OStorage
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.command.ext.lock.support.LockCases
import ru.vyarus.guice.persist.orient.repository.command.ext.lock.support.ext.CheckLockExtension
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class LockExecutionTest extends AbstractTest {

    @Inject
    LockCases dao

    def "Check lock binding"() {

        when: "checking that lock check works"
        dao.lock()
        then: 'check failed'
        thrown(RepositoryException)

        when: "call method with lock"
        CheckLockExtension.expected = OStorage.LOCKING_STRATEGY.KEEP_EXCLUSIVE_LOCK
        List<Model> res = dao.lock()
        then: "ok"
        res.size() == 10

        when: "call update method with lock"
        def cnt = dao.lockedUpdate("test")
        then: "ok"
        cnt == 10
    }
}