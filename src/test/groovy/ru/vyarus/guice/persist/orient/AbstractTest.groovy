package ru.vyarus.guice.persist.orient

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.Config
import spock.lang.Specification

import javax.inject.Inject

/**
 * NOTE: its normal to see logs like this (it will not be shown for plocal or remote connections):
 * "WARNING: Current implementation of storage does not support sbtree collections"
 *
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
abstract class AbstractTest extends Specification {
    @Inject
    PersistService persist
    @Inject
    PersistentContext<OObjectDatabaseTx> context

    void setup() {
        persist.start()
    }

    void cleanup() {
        context.doWithoutTransaction({ db ->
            for (Class<?> entity :
                    db.getEntityManager().getRegisteredEntities().findAll {
                        it.package.name.startsWith("ru.vyarus.guice")
                    }) {
                db.getEntityManager().deregisterEntityClass(entity)
            }
        } as SpecificTxAction<Void, OObjectDatabaseTx>)
        persist.stop()
        new ODatabaseDocumentTx(Config.DB).open(Config.USER, Config.PASS).drop()
    }
}
