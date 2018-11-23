package ru.vyarus.guice.persist.orient.db

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.exception.ODatabaseException
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Vyacheslav Rusakov
 * @since 21.10.2017
 */
@UseModules(PackageSchemeModule)
class ExternalTxTest extends AbstractTest {

    @Inject
    Provider<OrientDB> orientDB;


    def "Check external connection usage"() {

        setup: "manually creating connection"
        // using same orientdb object to simplify initialization
        def extDb = orientDB.get().open(Config.getDbName(), Config.USER, Config.PASS);

        when: "work in external unit of work"
        context.doInTransaction(TxConfig.external(), { db ->
            assert context.transactionManager.externalTransaction
            assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.NOTX
            assert extDb == db.underlying // same underlining instance
        } as SpecificTxAction)
        then: "connection remain after unit of work"
        ODatabaseRecordThreadLocal.instance().isDefined()
        !extDb.isClosed()

        cleanup:
        extDb?.close()
    }

    def "Check external connection rollback"() {

        setup: "manually creating connection"
        def extDb = orientDB.get().open(Config.getDbName(), Config.USER, Config.PASS);

        when: "throw exception during unit of work"
        context.doInTransaction(TxConfig.external(), { db ->
            throw new IllegalStateException("UPS")
        } as SpecificTxAction)
        then: "external connection wasn't rolled back"
        thrown(IllegalStateException)
        !extDb.isClosed()


        cleanup:
        extDb?.close()
    }

    def "Check external unit did not start without thread bound connection"() {

        when: "Creating external config without bound connection"
        assert !ODatabaseRecordThreadLocal.instance().isDefined()
        TxConfig.external()
        then: "error"
        thrown(ODatabaseException)
    }

    def "Check optimistic tx recognition"() {

        setup: "manually creating connection"
        def extDb = orientDB.get().open(Config.getDbName(), Config.USER, Config.PASS);
        extDb.begin(OTransaction.TXTYPE.OPTIMISTIC)

        when: "work in external unit of work"
        context.doInTransaction(TxConfig.external(), { db ->
            assert context.transactionManager.externalTransaction
            assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.OPTIMISTIC
        } as SpecificTxAction)
        then: "connection remain after unit of work"
        ODatabaseRecordThreadLocal.instance().isDefined()

        cleanup:
        extDb?.close()
    }

    def "Check external connection close in between"() {

        setup: "manually creating connection"
        def extDb = orientDB.get().open(Config.getDbName(), Config.USER, Config.PASS);

        when: "work in external unit of work"
        context.doInTransaction(TxConfig.external(), { db ->
            db.close()
        } as SpecificTxAction)
        then: "close detected"
        thrown(IllegalStateException)

        cleanup:
        extDb?.close()
    }
}
