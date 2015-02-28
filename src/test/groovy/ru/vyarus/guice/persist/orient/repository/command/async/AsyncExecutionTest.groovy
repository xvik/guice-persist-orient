package ru.vyarus.guice.persist.orient.repository.command.async

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.OCommandResultListener
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 28.02.2015
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class AsyncExecutionTest extends AbstractTest {

    @Inject
    AsyncCases repository

    def "Check async query"() {

        when: "calling async query"
        def res = []
        repository.select(new OCommandResultListener() {
            @Override
            boolean result(Object iRecord) {
                res << iRecord
                return true
            }

            @Override
            void end() {

            }
        })
        then: "query executed synchronously, async execution possible with remote only"
        res.size() == 10
    }

    def "Check error cases"() {
        OCommandResultListener dummy = new OCommandResultListener() {
            @Override
            boolean result(Object iRecord) {
                return false
            }

            @Override
            void end() {

            }
        }

        when: "query without listener"
        repository.noListener()
        then: "error"
        thrown(RepositoryException)

        when: "not void method"
        repository.notVoid(dummy)
        then: "error"
        thrown(RepositoryException)

        when: "not select query"
        repository.notSelect(dummy)
        then: "error"
        thrown(RepositoryException)
    }
}