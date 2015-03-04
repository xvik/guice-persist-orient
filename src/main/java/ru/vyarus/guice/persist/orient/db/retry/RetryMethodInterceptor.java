package ru.vyarus.guice.persist.orient.db.retry;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.orientechnologies.common.concur.ONeedRetryException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;

import javax.inject.Singleton;

/**
 * {@link Retry} annotation aop interceptor. Must be registered before {@link com.google.inject.persist.Transactional}.
 *
 * @author Vyacheslav Rusakov
 * @since 03.03.2015
 */
@Singleton
public class RetryMethodInterceptor implements MethodInterceptor {

    @Inject
    private TransactionManager transactionManager;

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Preconditions.checkState(!transactionManager.isTransactionActive(),
                "@Retry annotation makes no sense on method %s: because retry errors appear on commit and "
                        + "its impossible to catch them inside transaction.",
                RepositoryUtils.methodToString(invocation.getMethod()));
        final int retryCount = invocation.getMethod().getAnnotation(Retry.class).value();
        Preconditions.checkArgument(retryCount >= 1,
                "Bad @Retry annotation on method %s: retry count must be >= 1",
                RepositoryUtils.methodToString(invocation.getMethod()));
        int count = 0;
        Object res;
        while (true) {
            try {
                res = invocation.proceed();
                break;
            } catch (Throwable th) {
                count++;
                if (!isRetryException(th) || retryCount < count) {
                    throw th;
                }
            }
        }
        return res;
    }

    /**
     * Searching for {@link com.orientechnologies.common.concur.ONeedRetryException} in exception hierarchy.
     *
     * @param th thrown exception
     * @return true if retry could be performed
     */
    private boolean isRetryException(final Throwable th) {
        Throwable current = th;
        boolean res = false;
        while (current != null) {
            if (current instanceof ONeedRetryException) {
                res = true;
                break;
            }
            current = current.getCause();
        }
        return res;
    }
}
