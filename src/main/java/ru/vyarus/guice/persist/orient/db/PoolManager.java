package ru.vyarus.guice.persist.orient.db;

import com.google.inject.Provider;

/**
 * Manage pool of connections for specific connection type (e.g. object connections).
 * Pool also works as connection provider, but implementation should not allow to obtain connection outside transaction
 * (unit of work).
 * <p>While transaction manager defines global unit of work scope, each pool manager defines its own thread local transaction.
 * It is impossible in orient to share transaction between different connections (by design), but most likely in most cases only one pool will be used
 * in transaction.</p>
 * <p>To change pool size use {@code OGlobalConfiguration.DB_POOL_MIN.setValue()} and
 * {@code OGlobalConfiguration.DB_POOL_MAX.setValue()}. By default pools it's 1-20.</p>
 * <p>Do not use global pools in implementations - always create new one to avoid possible collisions with other pools</p>
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public interface PoolManager<T> extends Provider<T> {

    /**
     * Start pool.Will be called by PersistService implementation..
     *
     * @param uri  database uri
     * @param user database user
     * @param pass database password
     */
    void start(String uri, String user, String pass);

    /**
     * Stops pool. Will be called by PersistService implementation.
     */
    void stop();

    /**
     * Called by transaction manager to commit transaction at the end of unit of work.
     * Must do nothing if no connection where opened by pool (pool wasn't used).
     */
    void commit();

    /**
     * Called by transaction manager to rollback current connection.
     * Must do nothing if no connection where opened by pool (pool wasn't used)
     */
    void rollback();

    /**
     * @return pool type
     */
    DbType getType();
}
