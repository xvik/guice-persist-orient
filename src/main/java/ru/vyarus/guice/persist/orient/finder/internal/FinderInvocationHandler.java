package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.inject.Inject;
import com.google.inject.persist.finder.Finder;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.internal.AnnotationTxConfigBuilder;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Handler for generated proxy for finder interface.
 *
 * @author Vyacheslav Rusakov
 * @since 01.08.2014
 */
public class FinderInvocationHandler implements InvocationHandler {
    // field injection for delayed configuration (see module)
    @Inject
    FinderProxy finderProxy;
    @Inject
    TxTemplate template;
    @Inject
    TransactionManager transactionManager;

    public Object invoke(final Object thisObject, final Method method, final Object[] args)
            throws Throwable {

        // already inside transaction
        if (transactionManager.isTransactionActive()) {
            return process(thisObject, method, args);
        }

        // providing support for @Transactional annotations the same way as in other beans
        final TxConfig cfg = AnnotationTxConfigBuilder.buildConfig(method.getDeclaringClass(), method, false);

        // no tx defined
        if (cfg == null) {
            return process(thisObject, method, args);
        }

        // annotation find - do in transaction
        return template.doInTransaction(new TxAction<Object>() {
            @Override
            public Object execute() throws Throwable {
                return process(thisObject, method, args);
            }
        });
    }

    private Object process(final Object thisObject, final Method method, final Object[] args) throws Throwable {
        // Don't intercept non-finder methods like equals and hashcode.
        if (!method.isAnnotationPresent(Finder.class)) {
            // NOTE(dhanji): This is not ideal, we are using the invocation handler's equals
            // and hashcode as a proxy (!) for the proxy's equals and hashcode.
            return method.invoke(this, args);
        }

        return finderProxy.invoke(new MethodInvocation() {
            public Method getMethod() {
                return method;
            }

            public Object[] getArguments() {
                return null == args ? new Object[0] : args;
            }

            public Object proceed() throws Throwable {
                return method.invoke(thisObject, args);
            }

            public Object getThis() {
                throw new UnsupportedOperationException("Bottomless proxies don't expose a this.");
            }

            public AccessibleObject getStaticPart() {
                throw new UnsupportedOperationException();
            }
        });
    }
}
