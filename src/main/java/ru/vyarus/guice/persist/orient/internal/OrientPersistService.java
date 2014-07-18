package ru.vyarus.guice.persist.orient.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.model.ModelInitializer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class OrientPersistService implements PersistService, UnitOfWork, Provider<OObjectDatabaseTx> {
    private final Logger logger = LoggerFactory.getLogger(OrientPersistService.class);

    private String uri;
    private String user;
    private String pass;

    private ModelInitializer modelInitializer;

    // used to allow multiple start/stop calls (could be if service managed directly and PersistFilter registered)
    private boolean initialized = false;
    private OObjectDatabasePool pool;
    private ThreadLocal<OObjectDatabaseTx> transaction = new ThreadLocal<OObjectDatabaseTx>();
    private ThreadLocal<Integer> hierarchyLevel = new ThreadLocal<Integer>();


    @Inject
    public OrientPersistService(
            @Named("orient.uri") String uri,
            @Named("orient.user") String user,
            @Named("orient.password") String password,
            ModelInitializer modelInitializer) {
        this.uri = uri;
        this.user = user;
        this.pass = password;
        this.modelInitializer = modelInitializer;
    }

    @Override
    public OObjectDatabaseTx get() {
        return Preconditions.checkNotNull(transaction.get(), "Database usage outside of transaction");
    }

    @Override
    public void begin() {
        Preconditions.checkState(initialized, "Calling begin before initialization (call .start() before using transactions)");
        if (transaction.get() != null) {
            Integer level = hierarchyLevel.get();
            hierarchyLevel.set(++level);
            // transaction already in progress. no sub transactions native support in orient
            return;
        }
        hierarchyLevel.set(1);
        OObjectDatabaseTx db = pool.acquire();
        db.begin(OTransaction.TXTYPE.OPTIMISTIC);
        transaction.set(db);
    }

    @Override
    public void end() {
        Preconditions.checkState(isTransactionActive(), "No active transaction found to close");

        Integer level = hierarchyLevel.get();
        hierarchyLevel.set(--level);
        OObjectDatabaseTx db = transaction.get();
        if (level > 0) {
            // closing only overall transaction
            return;
        }

        try {
            // may not cause actual commit/close because force not used and pool may not decide to close connection
            db.commit().close();
        } finally {
            transaction.remove();
        }
    }

    public boolean isTransactionActive() {
        return transaction.get() != null;
    }

    public void rollback() {
        Preconditions.checkState(isTransactionActive(), "Call to rollback, while no active transaction");
        hierarchyLevel.remove();
        // force rollback for more predictable behaviour
        transaction.get().rollback(true).close();
        transaction.remove();
    }

    @Override
    public synchronized void start() {
        if (initialized) {
            logger.warn("Duplicate initialization prevented");
            return;
        }
        logger.debug("Initializing db: {}", uri);
        initialized = true;

        Preconditions.checkNotNull(uri, "Db name required");
        Preconditions.checkNotNull(user, "Db user name required");
        Preconditions.checkNotNull(pass, "Db user password required");

        // this mode perform a bit slower but provide more reliability
//        OGlobalConfiguration.TX_LOG_SYNCH.setValue(true);
//        OGlobalConfiguration.TX_COMMIT_SYNCH.setValue(true);

        // create if required (without creation work with db is impossible)
        OObjectDatabaseTx database = new OObjectDatabaseTx(uri);
        try {
            if (!database.exists()) {
                logger.info("Creating database: {}", uri);
                database.create();
            }
        } finally {
            database.close();
        }

        // main app pool
        pool = new OObjectDatabasePool(uri, user, pass);

        // one more transaction to initialize schema
        OObjectDatabaseTx db = pool.acquire();
        try {
            modelInitializer.initialize(db);
        } finally {
            db.commit().close();
        }
    }

    @Override
    public synchronized void stop() {
        if (!initialized) {
            // prevent double stop
            return;
        }
        initialized = false;
        if (pool != null) {
            logger.debug("Closing db pool: {}", uri);
            pool.close();
            pool = null;
        }
    }
}
