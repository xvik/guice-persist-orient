package ru.vyarus.guice.persist.orient.db.pool

import com.google.inject.Inject
import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.db.transaction.template.TemplateTransactionException
import ru.vyarus.guice.persist.orient.db.pool.support.MockPoolsModule
import ru.vyarus.guice.persist.orient.db.pool.support.pool.MockDocumentPool
import ru.vyarus.guice.persist.orient.db.pool.support.pool.MockObjectPool
import spock.guice.UseModules
import spock.lang.Specification

import java.nio.file.AccessDeniedException

/**
 * Check transaction manager correctly handles commit/rollback situations
 *
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
@UseModules(MockPoolsModule)
class PoolsTransactionTest extends Specification {

    @javax.inject.Inject
    PersistService persist
    @javax.inject.Inject
    TransactionManager transactionManager;
    @javax.inject.Inject
    SpecificTxTemplate<ODatabaseObject> template

    @Inject
    MockDocumentPool documentPool
    @Inject
    MockObjectPool objectPool

    void setup() {
        persist.start()
        // reset mocks state, because db on startup calls it
        documentPool.start(null)
        objectPool.start(null)
    }

    void cleanup() {
        persist.stop()
    }

    def "Check normal flow"() {

        when: "unit of work successful"
        template.doInTransaction({ db ->
            // do nothing
        } as SpecificTxAction)
        then: "pools committed"
        documentPool.committed
        objectPool.committed
    }

    def "Check general rollback"() {

        when: "unit of work failed"
        template.doInTransaction({ db ->
            throw new IllegalStateException()
        } as SpecificTxAction)
        then: "pools rolled back, exception propagated"
        thrown(IllegalStateException)
        documentPool.rolledBack
        objectPool.rolledBack
    }

    def "Check fail on commit"() {

        when: "only one unit of work successful"
        documentPool.onCommit = IllegalStateException
        template.doInTransaction({ db ->
            // do nothing
        } as SpecificTxAction)
        then: "failed pool rolled back, other one committed, commit fail propagated"
        thrown(IllegalStateException)
        documentPool.rolledBack
        objectPool.committed
    }

    def "Check fail on commit and then on rollback"() {

        when: "only one unit of work successful, other fail commit and then rollback"
        documentPool.onCommit = IllegalArgumentException
        documentPool.onRollback = IllegalArgumentException
        template.doInTransaction({ db ->
            // do nothing
        } as SpecificTxAction)
        then: "failed pool rolled back, other one committed, commit exception propagated"
        thrown(IllegalArgumentException)
        documentPool.rolledBack
        objectPool.committed
    }

    def "Check both pools fail"() {

        when: "both pools fail on commit, only last exception will be propagated"
        documentPool.onCommit = IllegalStateException
        objectPool.onCommit = IllegalArgumentException
        template.doInTransaction({ db ->
            // do nothing
        } as SpecificTxAction)
        then: "both pools rolled back, last commit exception propagated"
        thrown(IllegalStateException) // because commit on object pool called first
        documentPool.rolledBack
        objectPool.rolledBack
    }

    def "Check both rollbacks failed on exception"() {

        when: "both pools fail on commit, only last exception will be propagated"
        documentPool.onRollback = IllegalArgumentException
        objectPool.onRollback = IllegalArgumentException
        template.doInTransaction({ db ->
            throw new NullPointerException()
        } as SpecificTxAction)
        then: "both pools rolled back, original exception propagated"
        thrown(NullPointerException)
        documentPool.rolledBack
        objectPool.rolledBack
    }

    def "Check recover after exception, not matched rollbackOn"() {

        when: "unit of work failed"
        template.doInTransaction(new TxConfig([IllegalArgumentException], []), { db ->
            throw new NullPointerException()
        } as SpecificTxAction)
        then: "pools committed, recovered from exception, exception propagated"
        thrown(NullPointerException)
        documentPool.committed
        objectPool.committed
    }

    def "Check rollback on matched rollbackOn"() {

        when: "unit of work failed"
        template.doInTransaction(new TxConfig([NullPointerException], []), { db ->
            throw new NullPointerException()
        } as SpecificTxAction)
        then: "pools rolled back, exception propagated"
        thrown(NullPointerException)
        documentPool.rolledBack
        objectPool.rolledBack
    }

    def "Check recover after exception, matched ignore rule"() {

        when: "unit of work failed"
        template.doInTransaction(new TxConfig([], [NullPointerException]), { db ->
            throw new NullPointerException()
        } as SpecificTxAction)
        then: "pools committed, recovered from exception, exception propagated"
        thrown(NullPointerException)
        documentPool.committed
        objectPool.committed
    }

    def "Check recover after exception, because of ignorance precedence"() {

        when: "unit of work failed"
        template.doInTransaction(new TxConfig([IOException], [FileNotFoundException]), { db ->
            throw new FileNotFoundException()
        } as SpecificTxAction)
        then: "pools committed, recovered from exception, exception propagated"
        TemplateTransactionException ex = thrown()
        ex.getCause() instanceof FileNotFoundException
        documentPool.committed
        objectPool.committed
    }

    def "Check recover on matched ignore, because rollbackOn waits for more generic type"() {

        when: "unit of work failed"
        template.doInTransaction(new TxConfig([FileNotFoundException], [IOException]), { db ->
            throw new IOException()
        } as SpecificTxAction)
        then: "pools committed, recovered from exception, exception propagated"
        TemplateTransactionException ex = thrown()
        ex.getCause() instanceof IOException
        documentPool.committed
        objectPool.committed
    }

    def "Check rollback on generic rollbackBy match"() {

        when: "unit of work failed"
        template.doInTransaction(new TxConfig([IOException], [AccessDeniedException]), { db ->
            throw new FileNotFoundException()
        } as SpecificTxAction)
        then: "pools rolled back, exception propagated"
        TemplateTransactionException ex = thrown()
        ex.getCause() instanceof FileNotFoundException
        documentPool.rolledBack
        objectPool.rolledBack
    }
}