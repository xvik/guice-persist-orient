package ru.vyarus.guice.persist.orient.transaction

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.core.tx.OTransaction
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
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
        !transactionManager.isTransactionActive()
    }

    def "Check subtransaction"() {
        when: "do inline transactions"
        final Model model = insertService.subtransaction()
        then: "object correctly selected and no errors"
        model != null
        !transactionManager.isTransactionActive()
    }

    def "Check rollback"() {
        when: "Insert record and fail causing rollback"
        insertService.rollbackCheck()
        then: "Expect exception and no stored object in db"
        thrown(IllegalStateException)
        !transactionManager.isTransactionActive()
        selectService.select() == null
    }

    def "Check nested rollback"() {
        when: "Insert record and fail causing rollback"
        insertService.rollbackSubtransaction()
        then: "Expect exception and no stored object in db"
        thrown(IllegalStateException)
        !transactionManager.isTransactionActive()
        selectService.select() == null
    }

    def "Check tx inlining"() {
        when: "Executing tx callback inside another one and look that first one didn't close unit"
        template.doInTransaction({ db ->

            Model model = template.doInTransaction({ db2 ->
                db2.save(new Model(name: "tst", nick: "tst"))
            } as SpecificTxAction<Model, OObjectDatabaseTx>)
            // check we can perform actions (previous template not close connection
            List<Model> res = template.doInTransaction({ db2 ->
                db2.query(new OSQLSynchQuery<Model>("select * from Model where name=?"), model.getName())
            } as SpecificTxAction<List<Model>, OObjectDatabaseTx>)

            // check we can still use topmost connection
            Model stored = res[0]
            stored.setName("tst_chg")
            db.save(stored)
        } as SpecificTxAction<Void, OObjectDatabaseTx>)
        then: "everything ok"
        true
    }

    def "Check tx definition not changed by inlining"() {
        when: "Executing tx callback inside another one with changed tx type"
        template.doInTransaction({ db ->
            assert transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC

            template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db2 ->
                assert transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC
            } as SpecificTxAction<Void, OObjectDatabaseTx>)

            assert transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC
        } as SpecificTxAction<Void, OObjectDatabaseTx>)
        then: "everything ok"
        true
    }
}