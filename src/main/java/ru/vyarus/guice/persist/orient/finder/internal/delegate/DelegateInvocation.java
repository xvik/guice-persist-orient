package ru.vyarus.guice.persist.orient.finder.internal.delegate;

import ru.vyarus.guice.persist.orient.finder.internal.FinderExecutionException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Delegate finder invocation logic.
 *
 * @author Vyacheslav Rusakov
 * @since 26.10.2014
 */
public final class DelegateInvocation {

    private DelegateInvocation() {
    }

    public static Object processDelegate(final FinderDelegateDescriptor descriptor,
                                         final Method method, final Object[] arguments,
                                         final Object finder) throws Throwable {
        Object[] args;
        try {
            args = prepareArguments(descriptor, arguments, finder);
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format(
                    "Failed to prepare arguments for finder method %s#%s%s with arguments: %s",
                    descriptor.finderRootType, method.getName(), Arrays.toString(method.getParameterTypes()),
                    Arrays.toString(arguments)), ex);
        }
        try {
            final Object instance = descriptor.instanceProvider.get();
            return descriptor.method.method.invoke(instance, args);
        } catch (Throwable th) {
            throw new FinderExecutionException(String.format(
                    "Failed to invoke delegate method %s#%s%s of finder %s#%s%s with parameters %s",
                    descriptor.method.target.getName(), descriptor.method.method.getName(),
                    Arrays.toString(descriptor.method.method.getParameterTypes()),
                    descriptor.finderRootType, method.getName(), Arrays.toString(method.getParameterTypes()),
                    Arrays.toString(args)
            ), th);
        }
    }

    private static Object[] prepareArguments(final FinderDelegateDescriptor descriptor,
                                             final Object[] arguments, final Object finder) {
        final Integer connection = descriptor.method.connectionPosition;
        final Integer instance = descriptor.method.instancePosition;
        final Map<Integer, Class> typeParams = descriptor.method.typeParams;
        final int size = descriptor.method.method.getParameterTypes().length;
        final Object[] res = new Object[size];
        int shift = 0;
        for (int i = 0; i < size; i++) {
            if (connection != null && connection == i) {
                shift++;
                res[i] = descriptor.executor.getConnection();
            } else if (instance != null && instance == i) {
                shift++;
                res[i] = finder;
            } else if (typeParams != null && typeParams.containsKey(i)) {
                shift++;
                res[i] = typeParams.get(i);
            } else {
                res[i] = arguments[i - shift];
            }
        }
        return res;
    }
}
