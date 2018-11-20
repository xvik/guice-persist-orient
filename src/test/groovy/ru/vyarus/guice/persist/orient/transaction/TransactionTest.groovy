package ru.vyarus.guice.persist.orient.transaction

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import ru.vyarus.guice.persist.orient.support.service.InsertTransactionalService
import ru.vyarus.guice.persist.orient.support.service.SelectTransactionalService
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 19.07.2014
 */
@UseModules(PackageSchemeModule)
class TransactionTest extends AbstractTest {

    @Inject
    InsertTransactionalService insertService
    @Inject
    SelectTransactionalService selectService

    def "Check transaction declarations"() {
        when: "insert record in one transaction"
        insertService.insertRecord()
        then: "select record in another transaction and check transaction closed"
        selectService.select() != null
        !context.transactionManager.isTransactionActive()
    }

    def "Check subtransaction"() {
        when: "do inline transactions"
        final Model model = insertService.subtransaction()
        then: "object correctly selected and no errors"
        model != null
        !context.transactionManager.isTransactionActive()
    }

    def "Check rollback"() {
        when: "Insert record and fail causing rollback"
        insertService.rollbackCheck()
        then: "Expect exception and no stored object in db"
        thrown(IllegalStateException)
        !context.transactionManager.isTransactionActive()
        selectService.select() == null
    }

    def "Check nested rollback"() {
        when: "Insert record and fail causing rollback"
        insertService.rollbackSubtransaction()
        then: "Expect exception and no stored object in db"
        thrown(IllegalArgumentException)
        !context.transactionManager.isTransactionActive()
        selectService.select() == null
    }

    def "Check tx inlining"() {
        when: "Executing tx callback inside another one and look that first one didn't close unit"
        context.doInTransaction({ db ->

            Model model = context.doInTransaction({ db2 ->
                db2.save(new Model(name: "tst", nick: "tst"))
            } as SpecificTxAction<Model, ODatabaseObject>)
            // check we can perform actions (previous template not close connection
            List<Model> res = context.doInTransaction({ db2 ->
                db2.query(new OSQLSynchQuery<Model>("select from Model where name=?"), model.getName())
            } as SpecificTxAction<List<Model>, ODatabaseObject>)

            // check we can still use topmost connection
            Model stored = res[0]
            stored.setName("tst_chg")
            db.save(stored)
        } as SpecificTxAction<Void, ODatabaseObject>)
        then: "everything ok"
        true
    }

    def "Check tx definition not changed by inlining"() {
        when: "Executing tx callback inside another one with changed tx type"
        context.doInTransaction({ db ->
            assert context.transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC

            context.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db2 ->
                assert context.transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC
            } as SpecificTxAction<Void, ODatabaseObject>)

            assert context.transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC
        } as SpecificTxAction<Void, ODatabaseObject>)
        then: "everything ok"
        true
    }

    def "Check rollback recover"() {
        when: "Insert record and fail not causing rollback"
        context.doInTransaction(new TxConfig([], [IllegalStateException]), { db ->
            insertService.rollbackCheck()
        } as SpecificTxAction<Void, ODatabaseObject>)
        then: "Expect successful commit, object in db and exception"
        thrown(IllegalStateException)
        !context.transactionManager.isTransactionActive()
        selectService.select() != null
    }

    def "Check rollback on exception type"() {
        when: "Insert record and fail causing rollback"
        context.doInTransaction(new TxConfig([IllegalArgumentException], []), { db ->
            insertService.rollbackSubtransaction()
        } as SpecificTxAction<Void, ODatabaseObject>)
        then: "Expect exception and no stored object in db"
        thrown(IllegalArgumentException)
        !context.transactionManager.isTransactionActive()
        selectService.select() == null
    }

    def "Check rollback on specific exception type"() {
        when: "Insert record and fail not causing rollback"
        context.doInTransaction(new TxConfig([IllegalStateException], []), { db ->
            insertService.rollbackSubtransaction()
        } as SpecificTxAction<Void, ODatabaseObject>)
        then: "Expect exception and no stored object in db"
        thrown(IllegalArgumentException)
        !context.transactionManager.isTransactionActive()
        selectService.select() != null
    }

    def "TxConfig toString"() {

        when: "toString config"
        println new TxConfig([IllegalStateException], []).toString()
        then: "ok"
        true
    }
}