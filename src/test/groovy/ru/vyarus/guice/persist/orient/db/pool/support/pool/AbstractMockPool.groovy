package ru.vyarus.guice.persist.orient.db.pool.support.pool

import ru.vyarus.guice.persist.orient.db.pool.PoolManager

/**
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
abstract class AbstractMockPool<T> implements PoolManager<T> {

    boolean started
    boolean committed
    boolean rolledBack

    Class<RuntimeException> onCommit
    Class<RuntimeException> onRollback

    @Override
    void start(String database) {
        started = true
        committed = false
        rolledBack = false
        onCommit = null
        onRollback = null
    }

    @Override
    void stop() {
        started = false
    }

    @Override
    void commit() {
        // pools must be implemented in ignorance
        if (committed || rolledBack) return

        committed = true
        rolledBack = false
        if (onCommit != null) {
            committed = false;
            throw onCommit.newInstance()
        }
    }

    @Override
    void rollback() {
        // pools must be implemented in ignorance
        if (committed || rolledBack) return

        committed = false
        rolledBack = true
        // even if exception thrown on rollback its unrecoverable point
        if (onRollback != null) {
            throw onRollback.newInstance()
        }
    }

    @Override
    T get() {
        return null
    }
}
