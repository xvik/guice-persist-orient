package ru.vyarus.guice.persist.orient.transaction

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.TemplateTransactionException
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * Check txtemplate correctly handle exceptions
 *
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(DefaultModule)
class TxTemplatesExceptionsTest extends AbstractTest {

    @Inject
    PersistentContext<ODatabaseObject> context

    def "Check runtime exception"() {

        when: "checked exception thrown, it's wrapped"
        context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                throw new IllegalArgumentException()
            }
        })
        then: "runtime exception"
        thrown(IllegalArgumentException)
    }

    def "Check inline direct exception"() {

        when: "exception thrown from inlined template"
        context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                context.doInTransaction(new TxAction() {
                    @Override
                    Object execute() throws Throwable {
                        throw new IllegalArgumentException()
                    }
                })
            }
        })
        then: "exception rethrown as is"
        thrown(IllegalArgumentException)
    }

    def "Check checked exception"() {

        when: "checked exception thrown, it's wrapped"
        context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                throw new IOException()
            }
        })
        then: "runtime exception"
        def ex = thrown(TemplateTransactionException)
        ex.getCause().class == IOException
    }

    def "Check inline direct checked exception"() {

        when: "checked exception thrown from inlined template"
        context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                context.doInTransaction(new TxAction() {
                    @Override
                    Object execute() throws Throwable {
                        throw new IOException()
                    }
                })
            }
        })
        then: "exception wrapped"
        def ex = thrown(TemplateTransactionException)
        ex.cause.class == IOException
    }
}