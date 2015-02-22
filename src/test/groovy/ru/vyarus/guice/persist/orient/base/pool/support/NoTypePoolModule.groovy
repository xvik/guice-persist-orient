package ru.vyarus.guice.persist.orient.base.pool.support

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.base.pool.support.pool.FailStopPool
import ru.vyarus.guice.persist.orient.base.pool.support.pool.NoTypePool
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
        bindPool(ODatabaseDocumentTx, NoTypePool);
    }
}
