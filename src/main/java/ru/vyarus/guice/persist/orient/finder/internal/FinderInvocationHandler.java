package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.internal.AnnotationTxConfigBuilder;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate;
import ru.vyarus.guice.persist.orient.finder.util.FinderUtils;

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
    private FinderProxy finderProxy;
    @Inject
    private TxTemplate template;
    @Inject
    private TransactionManager transactionManager;

    @Override
    public Object invoke(final Object thisObject, final Method method,
                         final Object... args) throws Throwable {

        Object res;
        // already inside transaction
        if (transactionManager.isTransactionActive()) {
            res = process(thisObject, method, args);
        } else {
            final Class targetType = thisObject.getClass().getInterfaces()[0];
            // providing support for @Transactional annotations the same way as in other beans
            final TxConfig cfg = AnnotationTxConfigBuilder.buildConfig(targetType, method, false);

            if (cfg == null) {
                // no tx defined
                res = process(thisObject, method, args);
            } else {
                // annotation find - do in transaction
                res = template.doInTransaction(new TxAction<Object>() {
                    @Override
                    public Object execute() throws Throwable {
                        return process(thisObject, method, args);
                    }
                });
            }
        }
        return res;
    }

    private Object process(final Object thisObject, final Method method, final Object... args) throws Throwable {
        Object res;
        // Don't intercept non-finder methods like equals and hashcode.
        if (!FinderUtils.isFinderMethod(method)) {
            // NOTE(dhanji): This is not ideal, we are using the invocation handler's equals
            // and hashcode as a proxy (!) for the proxy's equals and hashcode.
            res = method.invoke(this, args);
        } else {
            res = finderProxy.invoke(new ProxyMethodInvocation(method, args, thisObject));
        }

        return res;
    }

    /**
     * Proxy method invocation object.
     */
    private static class ProxyMethodInvocation implements MethodInvocation {
        private final Method method;
        private final Object[] args;
        private final Object thisObject;

        public ProxyMethodInvocation(final Method method, final Object[] args, final Object thisObject) {
            this.method = method;
            this.args = args;
            this.thisObject = thisObject;
        }

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
            return thisObject;
        }

        public AccessibleObject getStaticPart() {
            throw new UnsupportedOperationException();
        }
    }
}
