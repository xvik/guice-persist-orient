package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.finder.internal.delegate.DelegateInvocation;
import ru.vyarus.guice.persist.orient.finder.internal.delegate.FinderDelegateDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.generics.FinderGenericsFactory;
import ru.vyarus.guice.persist.orient.finder.internal.generics.GenericsDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.query.FinderQueryDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.query.QueryInvocation;
import ru.vyarus.guice.persist.orient.finder.result.ResultConverter;
import ru.vyarus.guice.persist.orient.finder.result.ResultDesc;
import ru.vyarus.guice.persist.orient.finder.util.FinderUtils;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Implements finder analysis and query execution logic.
 * Method analysis delegated to descriptor factory.
 * Proxy just compose actual parameters, calls appropriate executor and converts result.
 * <p>Parse query and substitute query placeholders.
 * Placeholder format: select from MyObject where ${field} = ?.</p>
 * <p>You will need to mark parameters for substitution with
 * {@code ru.vyarus.guice.persist.orient.finder.query.Placeholder} annotation</p>
 * <p>String and enum could be used as placeholder values. For enum value .toString() used for string conversion.</p>
 * <p>Be careful with this feature: by doing substitution you easily allow malicious injections.
 * For guarding your substitution use enum placeholders. If you use string placeholder, use
 * {@code ru.vyarus.guice.persist.orient.finder.query.PlaceholderValues} to define possible values
 * (enum do such check for you). If more then one placeholder used, use
 * {@code ru.vyarus.guice.persist.orient.finder.query.Placeholders} to group PlaceholderValues annotations.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class FinderProxy implements MethodInterceptor {

    // field injection because instantiated directly in module
    @Inject
    private FinderGenericsFactory genericsFactory;
    @Inject
    private FinderDescriptorFactory factory;
    @Inject
    private ResultConverter resultConverter;

    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        final Class<?> finderType = FinderUtils.resolveFinderClass(methodInvocation.getThis());
        final GenericsDescriptor generics = genericsFactory.create(finderType);
        final Method method = methodInvocation.getMethod();
        final FinderDescriptor descriptor = getFinderDescriptor(method, generics);
        final Object[] arguments = methodInvocation.getArguments();
        final Object result = invoke(descriptor, method, arguments, methodInvocation.getThis());
        final ResultDesc desc = new ResultDesc();
        desc.result = result;
        desc.entityClass = descriptor.result.entityType;
        desc.type = descriptor.result.returnType;
        desc.returnClass = descriptor.result.expectType;
        try {
            return resultConverter.convert(desc);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format(
                    "Failed to convert execution result (%s) of finder %s#%s%s",
                    result == null ? null : result.getClass(), descriptor.finderRootType,
                    method.getName(), Arrays.toString(method.getParameterTypes())), th);
        }
    }

    private FinderDescriptor getFinderDescriptor(final Method method, final GenericsDescriptor generics) {
        FinderDescriptor descriptor;
        try {
            descriptor = factory.create(method, generics);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format("Failed to analyze finder method %s#%s%s",
                    generics.root, method.getName(), Arrays.toString(method.getParameterTypes())), th);
        }
        return descriptor;
    }

    private Object invoke(final FinderDescriptor descriptor, final Method method,
                          final Object[] arguments, final Object instance) throws Throwable {
        return descriptor instanceof FinderQueryDescriptor
                ? QueryInvocation.processQuery((FinderQueryDescriptor) descriptor, method, arguments)
                : DelegateInvocation.processDelegate((FinderDelegateDescriptor) descriptor, method,
                arguments, instance);
    }
}
