package ru.vyarus.guice.persist.orient.db;

import com.google.inject.Provider;

/**
 * Manage pool of connections for specific connection type (e.g. object connections).
 * Pool also works as connection provider, but implementation should not allow to obtain connection outside transaction
 * (unit of work).
 * <p>While transaction manager defines global unit of work scope, each pool manager may define
 * its own thread local transaction.
 * In default pool implementations single document connection is shared between pools.</p>
 * <p>To change pool size use {@code OGlobalConfiguration.DB_POOL_MAX.setValue()}. By default pools it's 100.</p>
 * <p>Do not use global pools in implementations - always create new one to avoid possible
 * collisions with other pools</p>
 * <p>Implementation must rely on {@link ru.vyarus.guice.persist.orient.db.user.UserManager} for actual
 * user credentials.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 * @param <T> pool connection type
 */
public interface PoolManager<T> extends Provider<T> {

    /**
     * Start pool. Will be called by PersistService implementation.
     * Method is responsible for initialization, specific for connection type (this mean new no tx connection
     * must be created to let orient configure database for specific connection type (required for object and graph)
     *
     * @param uri  database uri
     */
    void start(String uri);

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
