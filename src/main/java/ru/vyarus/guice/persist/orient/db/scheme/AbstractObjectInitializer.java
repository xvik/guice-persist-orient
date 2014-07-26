package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.inject.Provider;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * Base class for object mapping initializers (jpa-like approach).
 * <p>Object initialization specific:
 * <ul>
 * <li>Orient ignore package, so class may be moved between packages</li>
 * <li>When entity field removed, orient will hold all data already stored in records of that type</li>
 * <li>When class renamed orient will register it as new entity and you will have to manually migrate old table
 * (or use sql commands to rename entity in db scheme)</li>
 * </ul></p>
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public abstract class AbstractObjectInitializer implements SchemeInitializer {

    private Provider<OObjectDatabaseTx> dbProvider;

    protected AbstractObjectInitializer(Provider<OObjectDatabaseTx> dbProvider) {
        this.dbProvider = dbProvider;
    }

    @Override
    public void initialize() {
        init(dbProvider.get());
    }

    /**
     * Called to init schema with predefined object connection.
     *
     * @param db object connection
     */
    protected abstract void init(OObjectDatabaseTx db);
}
