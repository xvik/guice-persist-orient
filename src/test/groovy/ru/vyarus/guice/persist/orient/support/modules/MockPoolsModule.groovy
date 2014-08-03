package ru.vyarus.guice.persist.orient.support.modules

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.pool.MockDocumentPool
import ru.vyarus.guice.persist.orient.support.pool.MockObjectPool

/**
 * Default setup with overridden pools
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
class MockPoolsModule extends OrientModule {

    MockPoolsModule() {
        super(Config.DB, Config.USER, Config.PASS)
    }

    @Override
    protected void configurePools() {
        bindPool(OObjectDatabaseTx, MockObjectPool);
        bindPool(ODatabaseDocumentTx, MockDocumentPool);
    }
}
