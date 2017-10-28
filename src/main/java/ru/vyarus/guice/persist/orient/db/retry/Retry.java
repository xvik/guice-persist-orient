package ru.vyarus.guice.persist.orient.db.retry;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Due to implementation specifics, some queries may fail during concurrent updates.
 * In such cases orient throws {@link com.orientechnologies.common.concur.ONeedRetryException}, which means
 * re-executing operation may succeed.
 * <p>
 * If exception is thrown on annotated method and exception itself or one of its causes is retry exception,
 * then method will be re-executed.
 * <p>
 * Annotation must be used on method outside of transaction, because retry error happens on transaction commit,
 * and the only way to catch it is to sit after transaction logic. Annotation is more prioritized than
 * {@link com.google.inject.persist.Transactional} and may be used on the same method with it.
 * <p>
 * To summarize: retry is not applied inside transaction, but re-execute transaction (which was rolled back
 * on previous iteration). Annotation may be used long before transactional method.
 * <p>
 * Use with caution, because in some place you can use annotated method from inside ongoing transaction
 * and it will fail because of transaction check. For simple update cases
 * {@link ru.vyarus.guice.persist.orient.repository.command.script.Script} may be used to fix
 * concurrent exceptions: {@code @Script("begin update ... commit)} (even without retry it could fix problem).
 *
 * @author Vyacheslav Rusakov
 * @since 03.03.2015
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Retry {

    /**
     * @return count of retries
     */
    int value();
}
