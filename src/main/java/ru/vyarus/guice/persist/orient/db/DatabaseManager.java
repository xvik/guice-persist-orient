package ru.vyarus.guice.persist.orient.db;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.persist.PersistService;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.tx.OTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.data.DataInitializer;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;

/**
 * Responsible for database lifecycle. Creates db if necessary and call schema and data initializers for just opened database.
 * Also, responsible for pools lifecycle.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer
 * @see ru.vyarus.guice.persist.orient.db.data.DataInitializer
 * @since 24.07.2014
 */
public class DatabaseManager implements PersistService {
    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private String uri;
    private String user;
    private String pass;

    private Set<PoolManager> pools;
    private SchemeInitializer modelInitializer;
    private DataInitializer dataInitializer;
    private TxTemplate txTemplate;

    // used to allow multiple start/stop calls (could be if service managed directly and PersistFilter registered)
    private boolean initialized = false;
    private Set<DbType> supportedTypes;

    @Inject
    public DatabaseManager(
            final @Named("orient.uri") String uri,
            final @Named("orient.user") String user,
            final @Named("orient.password") String password,
            final Set<PoolManager> pools,
            final SchemeInitializer modelInitializer,
            final DataInitializer dataInitializer,
            final TxTemplate txTemplate) {

        Preconditions.checkNotNull(uri, "Database name required");
        Preconditions.checkNotNull(user, "Database user name required");
        Preconditions.checkNotNull(password, "Database user password required");

        this.uri = uri;
        this.user = user;
        this.pass = password;
        this.pools = pools;
        this.modelInitializer = modelInitializer;
        this.dataInitializer = dataInitializer;
        this.txTemplate = txTemplate;
    }

    @Override
    public void start() {
        if (initialized) {
            logger.warn("Duplicate initialization prevented. Check your initialization logic: " +
                    "persistent service should not be started two or more times");
            return;
        }

        createIfRequired();
        startPools();
        logger.debug("Registered types: {}", supportedTypes);
        logger.debug("Initializing database: '{}'", uri);
        // no tx (because of schema update - orient requirement)
        try {
            txTemplate.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), new TxAction<Void>() {
                @Override
                public Void execute() throws Throwable {
                    modelInitializer.initialize();
                    return null;
                }
            });
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new RuntimeException("Failed to initialize schema", throwable);
            }
        }

        // db ready to work
        initialized = true;
        // tx may be enabled (it's up to implementer)
        dataInitializer.initializeData();
    }

    @Override
    public void stop() {
        if (!initialized) {
            // prevent double stop
            return;
        }
        initialized = false;
        stopPools();
    }

    /**
     * @return set of supported database types (according to registered pools)
     */
    public Set<DbType> getSupportedTypes() {
        return ImmutableSet.copyOf(supportedTypes);
    }

    /**
     * @param type db type to check
     * @return true if db tpe supported (supporting pool regisntered), false otherwise
     */
    public boolean isTypeSupported(DbType type) {
        return supportedTypes.contains(type);
    }

    protected void createIfRequired() {
        // create if required (without creation work with db is impossible)
        ODatabaseDocumentTx database = new ODatabaseDocumentTx(uri);
        try {
            if (!database.exists()) {
                logger.info("Creating database: '{}'", uri);
                database.create();
            } else {
                database.open(user, pass);
            }
            initGraphDb(database);
        } finally {
            database.close();
        }
    }

    /**
     * Graph connection object performs schema check and can perform modifications.
     * To safely use graph connections later, initializing it with notx transaction.
     *
     * @param db notx document connection
     */
    protected void initGraphDb(ODatabaseDocumentTx db) {
        try {
            Class.forName("com.tinkerpop.blueprints.impls.orient.OrientGraph")
                    .getConstructor(ODatabaseDocumentTx.class).newInstance(db);
        } catch (ClassNotFoundException ignore) {
            // no graph support required
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to init graph connection", ex);
        }
    }

    protected void startPools() {
        supportedTypes = Sets.newHashSet();
        for (PoolManager<?> pool : pools) {
            // if pool start failed, entire app start should fail (no catch here)
            pool.start(uri, user, pass);
            supportedTypes.add(pool.getType());
        }
    }

    protected void stopPools() {
        for (PoolManager<?> pool : pools) {
            try {
                pool.stop();
            } catch (Throwable ex) {
                // continue to properly shutdown all pools
                logger.error("Pool '" + pool.getType() + "' shutdown failed ("+pool.getClass()+")", ex);
            }
        }
    }
}
