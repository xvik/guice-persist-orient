package ru.vyarus.guice.persist.orient.base.pool.support.pool

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.db.pool.PoolManager

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class FailStopPool implements PoolManager<ODatabaseDocumentTx> {

    @Override
    void start(String uri) {

    }

    @Override
    void stop() {
        throw new IllegalStateException("pool stop failed")
    }

    @Override
    void commit() {

    }

    @Override
    void rollback() {

    }

    @Override
    DbType getType() {
        return DbType.DOCUMENT
    }

    @Override
    ODatabaseDocumentTx get() {
        return null
    }
}
