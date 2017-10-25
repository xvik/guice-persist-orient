package ru.vyarus.guice.persist.orient.repository.command.async

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.OCommandResultListener
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.support.model.Model
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
            Object getResult() {
                return null
            }

            @Override
            boolean result(Object iRecord) {
                res << iRecord
                return true
            }

            @Override
            void end() {

            }
        })
        then: "query executed synchronously"
        res.size() == 10
    }

    def "Check non blocking async query"() {

        setup:
        // need more records for longer execution
        context.doInTransaction({db ->
            90.times({
                db.save(new Model(name: "name${10 + it}", nick: "nick${10 + it}"))
            })
        } as SpecificTxAction)

        when: "calling non blocking async query"
        def res = []
        def listener = new OCommandResultListener() {

            String threadName = Thread.currentThread().name

            @Override
            Object getResult() {
                return null
            }

            @Override
            boolean result(Object iRecord) {
                res << iRecord
                return true
            }

            @Override
            void end() {
                if (Thread.currentThread().name.equals(threadName)) {
                    println 'WARNING: listener called at the same thread as query'
                }
            }
        }
        repository.selectNonBlocking(listener)
        then: "immediate control"
        res.size() < 100

        when: "wait for result"
        sleep(100)
        then: "async results"
        res.size() == 100


        when: "non blocking query with future"
        res.clear()
        def future = repository.selectNonBlockingFuture(listener)
        then: "future returned"
        future != null
        res.size() < 100
        !future.isDone()

        when: "whit for future"
        future.get()
        then: "listener processed"
        res.size() == 100
    }

    def "Check error cases"() {
        OCommandResultListener dummy = new OCommandResultListener() {

            @Override
            Object getResult() {
                return null
            }

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

        // different logic branch so must be tested separately
        when: "not void non blocking method"
        repository.notVoidNonBlocking(dummy)
        then: "error"
        thrown(RepositoryException)

        when: "not select query"
        repository.notSelect(dummy)
        then: "error"
        thrown(RepositoryException)
    }
}