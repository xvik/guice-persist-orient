package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext

import com.google.inject.Inject
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.TestModel1
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
@UseModules(DefaultModule)
class ExtensionsExecutionsTest extends AbstractTest {

    @Inject
    ObjectSchemeInitializer initializer
    @Inject
    ExtensionsDescriptorFactory factory

    def "Check extensions execution"() {

        setup:
        context.transactionManager.begin(new TxConfig(OTransaction.TXTYPE.NOTX))

        when: "registering model with extensions"
        // using singleton extensions to check state
        def desc = factory.resolveExtensions(TestModel1)
        initializer.register(TestModel1)
        desc.type.each {
            assert it.extension.before
            assert it.extension.after
        }
        desc.fields.get("foo").each {
            assert it.extension.before
            assert it.extension.after
        }
        then: "extensions called"
        true

        cleanup:
        context.transactionManager.end()
    }
}