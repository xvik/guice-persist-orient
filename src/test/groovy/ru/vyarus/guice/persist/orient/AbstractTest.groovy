package ru.vyarus.guice.persist.orient

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.template.TransactionTemplate
import ru.vyarus.guice.persist.orient.template.TransactionalAction
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
abstract class AbstractTest extends Specification {
    @Inject
    PersistService persist
    @Inject
    TransactionTemplate template

    static {
        // transactions must be predictable in tests (sacrifice speed)
//        OGlobalConfiguration.TX_LOG_TYPE.setValue("mmap");
//        OGlobalConfiguration.TX_LOG_SYNCH.setValue(true);
//        OGlobalConfiguration.TX_COMMIT_SYNCH.setValue(true);
    }

    void setup() {
        persist.start()
    }

    void cleanup() {
        // drop db
        template.doWithTransaction({ db ->
            db.getStorage().clusterInstances.each({ it.delete() });
        } as TransactionalAction<OObjectDatabaseTx>)
        persist.stop()
    }
}
