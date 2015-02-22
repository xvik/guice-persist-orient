package ru.vyarus.guice.persist.orient.base.model.support

import com.google.inject.Inject
import com.google.inject.Provider
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.DatabaseManager
import ru.vyarus.guice.persist.orient.db.scheme.AbstractObjectInitializer

/**
 * @author Vyacheslav Rusakov 
 * @since 04.08.2014
 */
@javax.inject.Singleton
class ObjectSchemeTestInitializer extends AbstractObjectInitializer {

    @Inject
    ObjectSchemeTestInitializer(Provider<OObjectDatabaseTx> dbProvider, Provider<DatabaseManager> databaseManager) {
        super(dbProvider, databaseManager)
    }

    @Override
    protected void init(OObjectDatabaseTx db) {
    }

    // allow to trigger analysis manually in tests
    public void register(Class cls) {
        registerClass(cls)
    }
}
