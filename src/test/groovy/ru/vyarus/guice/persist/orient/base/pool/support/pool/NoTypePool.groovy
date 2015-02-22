package ru.vyarus.guice.persist.orient.base.pool.support.pool

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.db.pool.PoolManager

/**
 * Pool doesn't declare type.
 *
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
class NoTypePool implements PoolManager<ODatabaseDocumentTx> {

    @Override
    void start(String uri) {

    }

    @Override
    void stop() {

    }

    @Override
    void commit() {

    }

    @Override
    void rollback() {

    }

    @Override
    DbType getType() {
        return null
    }

    @Override
    ODatabaseDocumentTx get() {
        return null
    }
}
