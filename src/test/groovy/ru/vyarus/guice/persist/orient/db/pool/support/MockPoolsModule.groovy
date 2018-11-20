package ru.vyarus.guice.persist.orient.db.pool.support

import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.db.pool.support.pool.MockDocumentPool
import ru.vyarus.guice.persist.orient.db.pool.support.pool.MockObjectPool

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
        bindPool(ODatabaseObject, MockObjectPool);
        bindPool(ODatabaseDocument, MockDocumentPool);
    }
}
