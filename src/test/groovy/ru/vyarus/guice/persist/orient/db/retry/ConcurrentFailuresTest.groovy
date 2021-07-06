package ru.vyarus.guice.persist.orient.db.retry

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.retry.support.DangerUpdatesBean
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author Vyacheslav Rusakov 
 * @since 03.03.2015
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class ConcurrentFailuresTest extends AbstractTest {

    @Inject
    DangerUpdatesBean dao
    ExecutorService executor

    int threads = 2
    int operationsInThread = 20

    @Override
    void setup() {
        executor = Executors.newFixedThreadPool(threads)
    }

    @Override
    void cleanup() {
        executor.shutdown()
    }

    def "Check concurrent failure"() {

        when: "call concurrent updates for the same entities"
        boolean ok = doTest { dao.update(it) }
        then: "concurrent modification error"
        !ok

        when: "call concurrent updates with retry"
        ok = doTest { dao.updateWithRetry(it) }
        then: "retry helps"
        ok

        when: "call concurrent updates with script"
        ok = doTest { dao.updateWithScript(it) }
        then: "script also magically works"
        ok
    }

    def "Check retry cant be used inside transaction"() {

        when: "retry used inside transaction"
        context.doInTransaction({
            dao.updateWithRetry('sdsds')
        } as TxAction)
        then: 'error'
        thrown(IllegalStateException)
    }

    def "Check wrong retry value"() {

        when: "retry used inside transaction"
        dao.badRetry('sdsds')
        then: 'error'
        thrown(IllegalArgumentException)
    }

    boolean doTest(Closure action) {
        List<Future<Boolean>> executed = []
        threads.times({
            executed << executor.submit({
                boolean ok = true
                operationsInThread.times({
                    try {
                        action.call(UUID.randomUUID().toString())
                    } catch (Throwable th) {
                        println('Failed: ' + th.getMessage())
                        ok = false
                    }
                })
                return ok
            } as Callable<Boolean>)
        })
        boolean res = true
        // lock until finish
        executed.each({res = res && it.get() })
        return res
    }
}