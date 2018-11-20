package ru.vyarus.guice.persist.orient.db

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
@UseModules(DefaultModule)
class PersistentContextTest extends AbstractTest {

    // duplicate declaration, because groovy doesn't see inherited property in closures
    @Inject
    PersistentContext<ODatabaseObject> context

    def "Check configured templates"() {

        when: "executing template with context"
        context.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), new TxAction() {
            @Override
            Object execute() throws Throwable {
                assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.NOTX
                // trigger connection creation
                context.getConnection()
                return null
            }
        })
        then: "ok"
        true

        when: "executing specific template with context"
        context.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), new SpecificTxAction() {
            @Override
            Object execute(Object db) throws Throwable {
                assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.NOTX
                return null
            }
        })
        then: "ok"
        true
    }

    def "Check default templates"() {

        when: "executing template"
        context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.OPTIMISTIC
                // trigger connection creation
                context.getConnection()
                return null
            }
        })
        then: "ok"
        true

        when: "executing specific template"
        context.doInTransaction(new SpecificTxAction() {
            @Override
            Object execute(Object db) throws Throwable {
                assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.OPTIMISTIC
                return null
            }
        })
        then: "ok"
        true
    }

    def "Check notx templates"() {

        when: "executing template without transaction"
        context.doWithoutTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.NOTX
                // trigger connection creation
                context.getConnection()
                return null
            }
        })
        then: "ok"
        true

        when: "executing specific template without transaction"
        context.doWithoutTransaction(new SpecificTxAction() {
            @Override
            Object execute(Object db) throws Throwable {
                assert context.transactionManager.activeTransactionType == OTransaction.TXTYPE.NOTX
                return null
            }
        })
        then: "ok"
        true
    }

    def "Check notx mode fail under transaction"() {

        when: "executing notx action under transaction"
        context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                context.doWithoutTransaction(new TxAction() {
                    @Override
                    Object execute() throws Throwable {
                        return null
                    }
                })
                return null
            }
        })
        then: "fail because of ongoing transaction"
        thrown(IllegalStateException)
    }
}