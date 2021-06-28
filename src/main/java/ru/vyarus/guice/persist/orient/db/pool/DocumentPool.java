package ru.vyarus.guice.persist.orient.db.pool;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.user.UserManager;

/**
 * Document pool implementation.
 * Connection may be obtained (using provider) only inside unit of work (defined transaction).
 * Because of possible multi-transaction paradigm (default pools now use single transaction, but as before
 * pools implementation may be overridden to mimic legacy behaviour) inside single unit of work,
 * transaction manager always calls commit and rollback on all pools (no matter which one is really need it).
 * Implementation handle it by ignoring redundant calls: first commit or rollback finish pool transaction
 * and all other calls to commit or rollback simply ignored.
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public class DocumentPool implements PoolManager<ODatabaseDocument> {
    private final Logger logger = LoggerFactory.getLogger(DocumentPool.class);

    private final Provider<OrientDB> orientDB;
    private final TransactionManager transactionManager;
    private final UserManager userManager;
    private final ThreadLocal<ODatabaseDocument> transaction = new ThreadLocal<>();
    private ODatabasePool pool;
    private String database;

    @Inject
    public DocumentPool(final Provider<OrientDB> orientDB,
                        final TransactionManager transactionManager,
                        final UserManager userManager) {
        this.orientDB = orientDB;
        this.transactionManager = transactionManager;
        this.userManager = userManager;
    }

    @Override
    public void start(final String database) {
        this.database = database;
        pool = new ODatabasePool(orientDB.get(), database, userManager.getUser(), userManager.getPassword());
        // check database connection
        pool.acquire().close();
        logger.debug("Pool {} started for database '{}'", getType(), this.database);
    }

    @Override
    @SuppressWarnings("PMD.NullAssignment")
    public void stop() {
        if (pool != null) {
            pool.close();
            pool = null;
            logger.debug("Pool {} closed for database '{}'", getType(), database);
            database = null;
        }
    }

    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void commit() {
        final ODatabaseDocument db = transaction.get();
        if (db == null) {
            // pool not participate in current transaction
            return;
        }
        // this is an error for external transaction too because external unit of work must end
        // before manual connection close
        if (db.isClosed()) {
            // connection was closed manually, no need for rollback
            transaction.remove();
            checkOpened(db);
        }
        if (!transactionManager.isExternalTransaction()) {
            if (ODatabaseRecordThreadLocal.instance().get() != db) {
                logger.info("Connection '{}' should be assigned to thread '{}', but '{}' assigned. This indicates "
                                + "manual db.activateOnCurrentThread() calls. Might not be a problem. "
                                + "Binding correct connection.", db, Thread.currentThread().getName(),
                        ODatabaseRecordThreadLocal.instance().get()
                );
                // override orient internal connections usages (e.g. with live queries and embedded server)
                db.activateOnCurrentThread();
            }
            // may not cause actual commit/close because force parameter not used
            // in case of commit exception, transaction manager must perform rollback
            // (and close will take effect in rollback)
            db.commit();
            db.close();
        }
        transaction.remove();
        logger.trace("Pool {} commit successful", getType());
    }

    @Override
    @SuppressWarnings("PMD.UseTryWithResources")
    public void rollback() {
        final ODatabaseDocument db = transaction.get();
        if (db == null) {
            // pool not participate in current transaction or already committed (may happen if one other pool's
            // transaction fail: in this case all other transactions will be committed and after that
            // transactional manager call rollback, which will affect only failed pool and others will simply ignore it)
            return;
        }
        final boolean externalTransaction = transactionManager.isExternalTransaction();
        try {
            // do nothing fo external transaction
            if (!externalTransaction) {
                // may not cause actual rollback immediately because force not used
                checkOpened(db).rollback();
            }
            logger.trace("Pool {} rollback successful", getType());
        } finally {
            // don't touch external tx
            if (!externalTransaction && !db.isClosed()) {
                try {
                    // release connection back to pool in any case
                    db.close();
                } catch (Throwable ignored) {
                    logger.trace(String.format("Pool %s failed to close database", getType()), ignored);
                }
            }
            transaction.remove();
        }
    }

    @Override
    public ODatabaseDocument get() {
        // lazy get: pool transaction will start not together with TransactionManager one, but as soon as
        // connection requested to avoid using connections of not used pools
        Preconditions.checkNotNull(pool, String.format("Pool %s not initialized", getType()));
        if (transaction.get() == null) {
            Preconditions.checkState(transactionManager.isTransactionActive(), String.format(
                    "Can't obtain connection from pool %s: no transaction defined.", getType()));
            if (transactionManager.isExternalTransaction()) {
                // external mode: use already created connection
                transaction.set(ODatabaseRecordThreadLocal.instance().get());
                logger.trace("Pool {} use bound to thread connection (external mode)", getType());
            } else {
                // normal mode: create connection
                final ODatabaseDocument db = checkAndAcquireConnection();

                db.begin(transactionManager.getActiveTransactionType());
                transaction.set(db);
                logger.trace("Pool {} transaction started", getType());
            }
        }
        return (ODatabaseDocument) checkOpened(transaction.get()).activateOnCurrentThread();
    }

    /**
     * To early catch inconsistency errors it's better to check here (should reduce scope to search for problem).
     * It's so easy to call close directly on connection, but it shouldn't be done manually: either use unit of work
     * or completely manage connection yourself.
     *
     * @param db database connection instance
     * @return connection instance if its opened, otherwise error thrown
     */
    private ODatabaseDocument checkOpened(final ODatabaseDocument db) {
        Preconditions.checkState(orientDB.get().isOpen(), "Global OrientDB object is closed. "
                + "This must be the result of manual object closing.");
        Preconditions.checkState(!db.isClosed(), String.format(
                "Inconsistent %s pool state: thread-bound database closed! "
                        + "This may happen if close, commit or rollback was called directly on "
                        + "database connection object, which is not allowed (if you need full control "
                        + "on connection use manual setup and not pool managed connection)", getType()));
        return db;
    }

    /**
     * Its definitely not normal that pool returns closed connections, but possible if used improperly.
     *
     * @return connection itself or new valid connection
     */
    private ODatabaseDocument checkAndAcquireConnection() {
        final ODatabaseDocument res;
        if (userManager.isSpecificUser()) {
            // non pool-managed connection for different user
            res = orientDB.get().open(database, userManager.getUser(), userManager.getPassword());
        } else {
            res = pool.acquire();
        }

        if (res.isClosed()) {
            final String message = String.format(
                    "Pool %s return closed connection something is terribly wrong", getType());
            logger.error(message);
            throw new IllegalStateException(message);
        }
        return res;
    }

    @Override
    public DbType getType() {
        return DbType.DOCUMENT;
    }
}
