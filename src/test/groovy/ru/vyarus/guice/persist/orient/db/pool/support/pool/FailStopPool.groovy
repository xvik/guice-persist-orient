package ru.vyarus.guice.persist.orient.db.pool.support.pool

import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.db.pool.PoolManager

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class FailStopPool implements PoolManager<ODatabaseDocument> {

    @Override
    void start(String database) {

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
    ODatabaseDocument get() {
        return null
    }
}
