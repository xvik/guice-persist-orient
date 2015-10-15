package ru.vyarus.guice.persist.orient

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.util.uniquedb.UniqueDb
import spock.lang.Specification

import javax.inject.Inject

/**
 * NOTE: its normal to see logs like this (it will not be shown for plocal or remote connections):
 * "WARNING: Current implementation of storage does not support sbtree collections"
 *
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UniqueDb
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
        if (!Config.DB.contains("remote")) {
            def db = new ODatabaseDocumentTx(Config.DB)
            if (db.exists()) {
                try {
                    db.open(Config.USER, Config.PASS).drop()
                } catch (Exception ex) {
                    // manly resolves problem with lucene indexes
                    // at this point test already pass so its perfectly ok to forbid some orient errors
                    System.err.println("Db " + Config.DB + " drop error:");
                    ex.printStackTrace()
                }
            }
        }
        afterCleanup()
    }

    void afterCleanup() {
    }
}
