package ru.vyarus.guice.persist.orient.db.pool;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.PoolManager;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;

/**
 * Base class for default pool implementations.
 * Connection may be obtained (using provider) only inside unit of work (defined transaction).
 * Because of multi-transaction paradigm inside single unit of work, transaction manager always calls commit and rollback
 * on all pools (no matter which one is really need it). Implementation handle it by ignoring redundant calls:
 * first commit or rollback finish pool transaction and all other calls to commit or rollback simply ignored.
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public abstract class AbstractPool<T> implements PoolManager<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TransactionManager transactionManager;
    private ThreadLocal<ODatabaseComplex> transaction = new ThreadLocal<ODatabaseComplex>();
    private ODatabasePoolBase<?> pool;
    private String uri;

    protected AbstractPool(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void start(final String uri, final String user, final String pass) {
        pool = createPool(uri, user, pass);
        this.uri = uri;
        logger.debug("Pool created for '{}'", uri);
    }

    @Override
    public void stop() {
        if (pool != null) {
            pool.close();
            pool = null;
            logger.debug("Pool closed for '{}'", uri);
            uri = null;
        }
    }

    @Override
    public void commit() {
        final ODatabaseComplex db = transaction.get();
        if (db == null) {
            // pool not participate in current transaction
            return;
        }
        try {
            // may not cause actual commit/close because force not used
            db.commit().close();
            logger.trace("Pool commit successful");
        } finally {
            transaction.remove();
        }
    }

    @Override
    public void rollback() {
        final ODatabaseComplex db = transaction.get();
        if (db == null) {
            // pool not participate in current transaction or already committed (may happen if one other pool's transaction fail:
            // in this case all other transactions will be committed and after that transactional manager call rollback, which will affect
            // only failed pool and others will simply ignore it)
            return;
        }
        try {
            // may not cause actual rollback immediately because force not used
            db.rollback().close();
            logger.trace("Pool rollback successful");
        } finally {
            transaction.remove();
        }
    }

    @Override
    public T get() {
        // lazy get: pool transaction will start not together with TransactionManager one, but as soon as connection requested
        // to avoid using connections of not used pools
        Preconditions.checkNotNull(pool, "Pool not initialized");
        if (transaction.get() == null) {
            Preconditions.checkState(transactionManager.isTransactionActive(),
                    "Connection must be obtained inside transaction only");
            final ODatabaseComplex db = (ODatabaseComplex) pool.acquire();
            db.begin(transactionManager.getActiveTransactionType());
            transaction.set(db);
            logger.trace("Pool transaction started");
        }
        return convertDbInstance(transaction.get());
    }

    /**
     * Called to create correct orient pool instance (native orient pool)
     *
     * @param uri  database url
     * @param user database user
     * @param pass database password
     * @return orient connection pool instance
     */
    protected abstract ODatabasePoolBase createPool(String uri, String user, String pass);

    /**
     * @param db connection instance obtained from pool or thread local
     * @return converted connection instance (e.g. wrapped as in graph pool) or connection without modification
     * if method not overridden in pool
     */
    @SuppressWarnings("unchecked")
    protected T convertDbInstance(ODatabaseComplex<?> db) {
        return (T) db;
    }
}
