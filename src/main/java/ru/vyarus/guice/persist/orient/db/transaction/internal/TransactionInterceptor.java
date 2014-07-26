package ru.vyarus.guice.persist.orient.db.transaction.internal;


import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.tx.OTransaction;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxType;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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
        if (transactionManager.isTransactionActive()) {
            logger.trace("Annotated method {} already in transaction", invocation.getMethod().getName());
            return invocation.proceed();
        }

        final TxConfig config = buildConfig(invocation);
        logger.trace("Starting transaction for annotated method {}", invocation.getMethod().getName());
        return template.doInTransaction(config, new TxAction<Object>() {
            @Override
            public Object execute() throws Throwable {
                return invocation.proceed();
            }
        });
    }

    private TxConfig buildConfig(MethodInvocation methodInvocation) {
        Transactional transactional = findAnnotation(methodInvocation, Transactional.class);
        TxType txType = findAnnotation(methodInvocation, TxType.class);
        return new TxConfig(wrapExceptions(transactional.rollbackOn()),
                wrapExceptions(transactional.ignore()), txType.value());
    }

    private <T extends Annotation> T findAnnotation(MethodInvocation methodInvocation, Class<T> target) {
        T transactional;
        Method method = methodInvocation.getMethod();
        Class<?> targetClass = methodInvocation.getThis().getClass();

        transactional = method.getAnnotation(target);
        if (null == transactional) {
            // If none on method, try the class.
            transactional = targetClass.getAnnotation(target);
        }
        if (null == transactional) {
            // If there is no transactional annotation present, use the default
            transactional = Internal.class.getAnnotation(target);
        }

        return transactional;
    }

    /**
     * Avoid creation of empty list
     *
     * @param list array of exceptions
     * @return converted list or null if array os empty
     */
    private List<Class<? extends Exception>> wrapExceptions(Class<? extends Exception>[] list) {
        return list.length == 0 ? null : Arrays.asList(list);
    }

    @Transactional
    @TxType(OTransaction.TXTYPE.OPTIMISTIC)
    private static class Internal {
    }
}
