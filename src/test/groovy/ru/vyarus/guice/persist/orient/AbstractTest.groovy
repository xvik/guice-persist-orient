package ru.vyarus.guice.persist.orient

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.metadata.security.OSecurity
import com.orientechnologies.orient.core.metadata.security.OSecurityNull
import com.orientechnologies.orient.core.security.OSecurityFactory
import com.orientechnologies.orient.core.security.OSecurityManager
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
        setupSecurity()
        persist.start()
        println 'persistence started'
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
                println 'obtain null security'
                return new OSecurityNull(null, null)
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
                db.open(Config.USER, Config.PASS).drop()
            }
        }
        afterCleanup()
        println 'persistence cleaned'
    }

    void afterCleanup() {
    }
}
