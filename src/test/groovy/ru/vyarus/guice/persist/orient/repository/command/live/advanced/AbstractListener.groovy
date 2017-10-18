package ru.vyarus.guice.persist.orient.repository.command.live.advanced

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener
import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.RecordOperation

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 12.10.2017
 */
abstract class AbstractListener<T> implements LiveQueryListener<T> {

    int lastToken
    RecordOperation lastOp
    T last
    boolean unsubscribed
    boolean errored

    @Inject
    PersistentContext<OObjectDatabaseTx> context;

    @Override
    void onLiveResult(int token, RecordOperation operation, T result) throws Exception {
        lastToken = token
        lastOp = operation
        last = postProcess(result)
    }

    protected T postProcess(T result) {
        return result;
    }

    @Override
    void onError(int token) {
        errored = true
    }

    @Override
    void onUnsubscribe(int token) {
        unsubscribed = true
    }

    void reset() {
        last = null
        lastToken = 0
        lastOp = null
    }
}
