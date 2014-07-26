package ru.vyarus.guice.persist.orient

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import org.slf4j.bridge.SLF4JBridgeHandler
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import spock.lang.Specification

import javax.inject.Inject
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

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
    TransactionManager transactionManager;
    @Inject
    SpecificTxTemplate<OObjectDatabaseTx> template

    static {
        // transactions must be predictable in tests (sacrifice speed)
//        OGlobalConfiguration.TX_LOG_TYPE.setValue("mmap");
//        OGlobalConfiguration.TX_LOG_SYNCH.setValue(true);
//        OGlobalConfiguration.TX_COMMIT_SYNCH.setValue(true);
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
        Logger.getLogger("global").setLevel(Level.WARNING);
    }

    void setup() {
        persist.start()
    }

    void cleanup() {
        // drop db
        template.doInTransaction({ db ->
            db.getStorage().clusterInstances.each({ it.delete() });
        } as SpecificTxAction<Void, OObjectDatabaseTx>)
        persist.stop()
    }
}
