package ru.vyarus.guice.persist.orient.db.transaction.internal;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate;

import javax.inject.Inject;
import java.lang.reflect.Method;

/**
 * Implements @Transaction annotation interception logic.
 */
public class TransactionInterceptor implements MethodInterceptor {
    private final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);

    // field injection for delayed configuration (see module)
    @Inject
    private TxTemplate template;
    @Inject
    private TransactionManager transactionManager;


    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        // checking directly to avoid redundant objects creation (simplify stacktrace)
        final Method method = invocation.getMethod();
        final Object res;
        if (transactionManager.isTransactionActive()) {
            logger.trace("Annotated method {} already in transaction", method.getName());
            res = invocation.proceed();
        } else {
            final TxConfig config = AnnotationTxConfigBuilder
                    .buildConfig(invocation.getThis().getClass(), method, true);
            logger.trace("Starting transaction for annotated method {}", method.getName());
            res = template.doInTransaction(config, new TxAction<Object>() {
                @Override
                public Object execute() throws Throwable {
                    return invocation.proceed();
                }
            });
        }
        return res;
    }
}
