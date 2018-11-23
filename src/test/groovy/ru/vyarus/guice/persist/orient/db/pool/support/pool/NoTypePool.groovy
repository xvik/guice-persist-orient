package ru.vyarus.guice.persist.orient.db.pool.support.pool

import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.db.pool.PoolManager

/**
 * Pool doesn't declare type.
 *
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
class NoTypePool implements PoolManager<ODatabaseDocument> {

    @Override
    void start(String database) {

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
    ODatabaseDocument get() {
        return null
    }
}
