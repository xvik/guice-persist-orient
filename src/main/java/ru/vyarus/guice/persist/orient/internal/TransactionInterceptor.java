package ru.vyarus.guice.persist.orient.internal;


import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.template.TransactionTemplate;
import ru.vyarus.guice.persist.orient.template.TransactionalAction;

import javax.inject.Inject;

public class TransactionInterceptor implements MethodInterceptor {

    // field injection for delayed configuration (see module)
    @Inject
    private TransactionTemplate template;

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        return template.doWithTransaction(new TransactionalAction<Object>() {
            @Override
            public Object execute(final OObjectDatabaseTx db) throws Throwable {
                return invocation.proceed();
            }
        });
    }
}
