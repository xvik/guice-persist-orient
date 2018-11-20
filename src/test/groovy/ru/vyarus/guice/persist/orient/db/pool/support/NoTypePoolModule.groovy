package ru.vyarus.guice.persist.orient.db.pool.support

import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.db.pool.support.pool.NoTypePool
import ru.vyarus.guice.persist.orient.support.Config

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
class NoTypePoolModule extends OrientModule {

    NoTypePoolModule() {
        super(Config.DB, Config.USER, Config.PASS)
    }

    @Override
    protected void configurePools() {
        bindPool(ODatabaseDocument, NoTypePool);
    }
}
