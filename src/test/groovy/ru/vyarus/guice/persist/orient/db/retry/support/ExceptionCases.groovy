package ru.vyarus.guice.persist.orient.db.retry.support

import com.orientechnologies.common.concur.ONeedRetryException
import ru.vyarus.guice.persist.orient.db.retry.Retry

/**
 * @author Vyacheslav Rusakov 
 * @since 04.03.2015
 */
class ExceptionCases {

    int callCount;

    @Retry(10)
    void wrappedRetry() {
        callCount++
        throw new IllegalStateException(new REx())
    }

    @Retry(10)
    void otherException() {
        callCount++
        throw new IllegalStateException()
    }

    static class REx extends ONeedRetryException {
        REx() {
            super("retry")
        }
    }
}
