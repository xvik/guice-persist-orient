package ru.vyarus.guice.persist.orient

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.metadata.security.ORole
import com.orientechnologies.orient.core.metadata.security.OUser
import ru.vyarus.guice.persist.orient.db.OrientDBFactory
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
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
    PersistentContext<ODatabaseObject> context
    @Inject
    OrientDBFactory info

    void setup() {
        // TODO for now reverted 3.2 behaviours, but no-users mode should be supported directly
        OGlobalConfiguration.SCRIPT_POLYGLOT_USE_GRAAL.setValue(false)
        OGlobalConfiguration.CREATE_DEFAULT_USERS.setValue(true)

        persist.start()
    }

    void cleanup() {
        context.doWithoutTransaction({ db ->
            db.getEntityManager().getRegisteredEntities().findAll {
                it.package.name.startsWith("ru.vyarus.guice")
            }.each {
                db.getEntityManager().deregisterEntityClass(it)
            }
            db.getEntityManager().deregisterEntityClass(ORole)
            db.getEntityManager().deregisterEntityClass(OUser)
            db.getMetadata().getSchema().reload()
        } as SpecificTxAction<Void, ODatabaseObject>)
        persist.stop()
        def db = info.createOrientDB()
        if (db.exists(info.getDbName())) {
            db.drop(info.getDbName())
        }
        db.close()
        afterCleanup()
    }

    void afterCleanup() {
    }
}
