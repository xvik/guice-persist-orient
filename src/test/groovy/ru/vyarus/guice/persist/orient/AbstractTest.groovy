package ru.vyarus.guice.persist.orient

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.metadata.security.ORole
import com.orientechnologies.orient.core.metadata.security.OSecurity
import com.orientechnologies.orient.core.metadata.security.OUser
import com.orientechnologies.orient.core.security.OSecurityFactory
import com.orientechnologies.orient.core.security.OSecurityManager
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.OrientDBFactory
import ru.vyarus.guice.persist.orient.util.OSecurityNull
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
        setupSecurity()
        persist.start()
    }

    // could be overridden for different security config
    // no security will work for most cases
    void setupSecurity() {
        // switch off orient security which is very time consuming since 2.2
        OGlobalConfiguration.CREATE_DEFAULT_USERS.setValue(false)
        // don't override security for remote tests
        OSecurityManager.instance().securityFactory = new OSecurityFactory() {
            @Override
            OSecurity newSecurity() {
                return new OSecurityNull()
            }
        }
    }

    // intended to be called from overridden setupSecurity
    protected final void defaultSecurity() {
        OGlobalConfiguration.CREATE_DEFAULT_USERS.setValue(true)
        // reset default factory
        OSecurityManager.instance().securityFactory = null
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
