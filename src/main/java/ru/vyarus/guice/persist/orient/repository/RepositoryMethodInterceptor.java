package ru.vyarus.guice.persist.orient.repository;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.repository.core.MethodDescriptorFactory;
import ru.vyarus.guice.persist.orient.repository.core.result.converter.ResultConverter;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethodExtension;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Repository methods handler. Handler is attached (aop) by marker annotation
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod}. Actual method calls are
 * handled by specific method extensions
 * ({@link ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethodExtension}).
 * <p>General workflow: method descriptor is computed or cached instance used, method parameters parsed and
 * extension specific logic called, result is optionally converted using
 * {@link ru.vyarus.guice.persist.orient.repository.core.result.converter.ResultConverter}.</p>
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.repository.command.query.QueryMethodExtension
 * @see ru.vyarus.guice.persist.orient.repository.delegate.DelegateMethodExtension
 * @since 30.07.2014
 */
@Singleton
public class RepositoryMethodInterceptor implements MethodInterceptor {

    // field injection because instantiated directly in module
    @Inject
    private MethodDescriptorFactory factory;
    @Inject
    private ResultConverter resultConverter;

    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        final Class<?> repositoryType = RepositoryUtils.resolveRepositoryClass(methodInvocation.getThis());
        final Method method = methodInvocation.getMethod();
        final RepositoryMethodDescriptor descriptor = getMethodDescriptor(method, repositoryType);
        final Object result = executeMethod(descriptor, methodInvocation);
        return convertResult(method, descriptor, result);
    }

    private RepositoryMethodDescriptor getMethodDescriptor(final Method method, final Class<?> type) {
        RepositoryMethodDescriptor descriptor;
        try {
            descriptor = factory.create(method, type);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format("Failed to analyze repository method %s#%s%s",
                    type, method.getName(), Arrays.toString(method.getParameterTypes())), th);
        }
        return descriptor;
    }

    @SuppressWarnings("unchecked")
    private Object executeMethod(final RepositoryMethodDescriptor descriptor,
                                 final MethodInvocation methodInvocation) {
        final Method method = methodInvocation.getMethod();
        final Object[] arguments = methodInvocation.getArguments();
        try {
            final RepositoryMethodExtension extension = (RepositoryMethodExtension) descriptor.methodExtension.get();
            return extension.execute(descriptor, methodInvocation.getThis(), arguments);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format(
                    "Failed to execute repository method %s#%s%s with arguments %s",
                    descriptor.repositoryRootType,
                    method.getName(), Arrays.toString(method.getParameterTypes()), Arrays.toString(arguments)), th);
        }
    }

    private Object convertResult(final Method method, final RepositoryMethodDescriptor descriptor,
                                 final Object result) {
        try {
            return resultConverter.convert(descriptor.result, result);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format(
                    "Failed to convert execution result (%s) of repository method %s#%s%s",
                    result == null ? null : result.getClass(), descriptor.repositoryRootType,
                    method.getName(), Arrays.toString(method.getParameterTypes())), th);
        }
    }
}
