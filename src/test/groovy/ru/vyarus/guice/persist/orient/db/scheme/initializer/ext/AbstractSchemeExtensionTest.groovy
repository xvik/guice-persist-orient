package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
@UseModules(DefaultModule)
abstract class AbstractSchemeExtensionTest extends AbstractTest {

    @Inject
    ObjectSchemeInitializer schemeInitializer
    ODatabaseObject db

    @Override
    void setup() {
        context.doWithoutTransaction({ db ->
            db.getEntityManager().deregisterEntityClasses(Config.MODEL_PKG)
            db.getEntityManager().deregisterEntityClasses(getModelPackage())
            db.getMetadata().getSchema().synchronizeSchema()
            db.getMetadata().getIndexManager().reload()
        } as SpecificTxAction<Void, ODatabaseObject>)
        schemeInitializer.clearModelCache()
        context.transactionManager.begin(new TxConfig(OTransaction.TXTYPE.NOTX))
        db = context.getConnection()
    }

    @Override
    void cleanup() {
        context.transactionManager.end()
    }

    abstract String getModelPackage()
}