package ru.vyarus.guice.persist.orient.transaction

import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.transaction.support.NoTxModule
import spock.guice.UseModules
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(NoTxModule)
class DefaultNoTxTest extends AbstractTest {

    def "Check default notx"() {

        when: "staring transaction"
        context.transactionManager.begin()
        then: "its started as notx"
        context.transactionManager.activeTransactionType == OTransaction.TXTYPE.NOTX

        cleanup:
        context.transactionManager.end()

    }
}