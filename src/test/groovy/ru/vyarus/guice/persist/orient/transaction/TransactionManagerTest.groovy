package ru.vyarus.guice.persist.orient.transaction

import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
@UseModules(DefaultModule)
class TransactionManagerTest extends AbstractTest {

    def "Check config tx manager inline transaction"() {

        when: "opening inline transaction with custom config"
        context.getTransactionManager().begin()
        assert context.transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC
        context.getTransactionManager().begin(new TxConfig(OTransaction.TXTYPE.NOTX))
        then: "config ignored"
        context.transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.OPTIMISTIC

        cleanup:
        context.transactionManager.end()

    }
}