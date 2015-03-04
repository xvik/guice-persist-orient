package ru.vyarus.guice.persist.orient.db.retry.support

import com.orientechnologies.common.concur.OTimeoutException
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
        throw new IllegalStateException(new OTimeoutException())
    }

    @Retry(10)
    void otherException() {
        callCount++
        throw new IllegalStateException()
    }
}
