package ru.vyarus.guice.persist.orient.concurrent

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import ru.vyarus.guice.persist.orient.support.service.InsertTransactionalService
import ru.vyarus.guice.persist.orient.support.service.SelectTransactionalService
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
@UseModules(PackageSchemeModule)
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
            return db.countClass(Model)
        } as SpecificTxAction<Long, OObjectDatabaseTx>)
        then: "Db should contain 20 records"
        !transactionManager.isTransactionActive()
        cnt == times
    }
}