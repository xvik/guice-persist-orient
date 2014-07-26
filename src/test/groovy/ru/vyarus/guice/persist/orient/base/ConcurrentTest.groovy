package ru.vyarus.guice.persist.orient.base

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.base.model.Model
import ru.vyarus.guice.persist.orient.base.modules.SimpleModule
import ru.vyarus.guice.persist.orient.base.service.InsertTransactionalService
import ru.vyarus.guice.persist.orient.base.service.SelectTransactionalService
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import spock.guice.UseModules

import javax.inject.Inject
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author Vyacheslav Rusakov 
 * @since 20.07.2014
 */
@UseModules(SimpleModule.class)
class ConcurrentTest extends AbstractTest {

    @Inject
    InsertTransactionalService insertService
    @Inject
    SelectTransactionalService selectService

    ExecutorService executor

    @Override
    void setup() {
        executor = Executors.newFixedThreadPool(20)
    }

    @Override
    void cleanup() {
        executor.shutdown()
    }

    def "Check concurrent rw"() {
        when: "Insert and select record in 20 threads"
        List<Future<?>> executed = []
        int times = 20
        times.times({
            executed << executor.submit(new Callable() {
                @Override
                Object call() throws Exception {
                    insertService.subtransaction()
                    return null
                }
            })
        })
        // lock until finish
        executed.each({ it.get() })

        Long cnt = template.doInTransaction({ db ->
            return db.countClass(Model.class)
        } as SpecificTxAction<Long, OObjectDatabaseTx>)
        then: "Db should contain 20 records"
        !transactionManager.isTransactionActive()
        cnt == times
    }
}