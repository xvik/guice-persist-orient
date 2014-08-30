package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.finder.command.SqlCommandDesc;
import ru.vyarus.guice.persist.orient.finder.result.ResultConverter;
import ru.vyarus.guice.persist.orient.finder.result.ResultDesc;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Implements finder analysis and query execution logic.
 * Method analysis delegated to descriptor factory.
 * Proxy just compose actual parameters, calls appropriate executor and converts result.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class FinderProxy implements MethodInterceptor {

    // field injection because instantiated directly in module
    @Inject
    private FinderDescriptorFactory factory;
    @Inject
    private ResultConverter resultConverter;

    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {

        final Method method = methodInvocation.getMethod();
        FinderDescriptor descriptor;
        try {
            descriptor = factory.create(method);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format(
                    "Failed to analyze finder method %s#%s%s",
                    method.getDeclaringClass(), method.getName(), Arrays.toString(method.getParameterTypes())), th);
        }

        final SqlCommandDesc command = buildCommand(descriptor, methodInvocation.getArguments());

        final Object result = descriptor.executor.executeQuery(command);

        final ResultDesc desc = new ResultDesc();
        desc.result = result;
        desc.entityClass = descriptor.returnEntity;
        desc.type = descriptor.returnType;
        desc.returnClass = descriptor.expectType;
        try {
            return resultConverter.convert(desc);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format(
                    "Failed to convert execution result (%s) of finder %s#%s%s",
                    result == null ? null : result.getClass(), method.getDeclaringClass(),
                    method.getName(), Arrays.toString(method.getParameterTypes())), th);
        }
    }

    private SqlCommandDesc buildCommand(final FinderDescriptor descriptor, final Object[] arguments) {
        final SqlCommandDesc command = new SqlCommandDesc();
        command.isFunctionCall = descriptor.isFunctionCall;
        command.function = descriptor.functionName;
        command.query = descriptor.query;
        command.useNamedParams = descriptor.useNamedParameters;
        if (command.useNamedParams) {
            command.namedParams = getNamedParams(descriptor.namedParametersIndex, arguments);
        } else {
            command.params = getParams(descriptor.parametersIndex, arguments);
        }

        switch (descriptor.returnType) {
            case COLLECTION:
            case ARRAY:
                assignPaginationParams(command, descriptor, arguments);
                break;
            case PLAIN:
                // implicitly limit query for single return result (for performance)
                command.max = 1;
                break;
            default:
                throw new IllegalStateException("Unsupported return type " + descriptor.returnType);
        }
        return command;
    }

    private void assignPaginationParams(final SqlCommandDesc command,
                                        final FinderDescriptor descriptor, final Object[] arguments) {
        if (descriptor.firstResultParamIndex != null) {
            final Number rawStartValue = (Number) arguments[descriptor.firstResultParamIndex];
            final Integer startValue = rawStartValue == null ? null : rawStartValue.intValue();
            command.start = Objects.firstNonNull(startValue, 0);
        }

        if (descriptor.maxResultsParamIndex != null) {
            final Number rawMaxValue = (Number) arguments[descriptor.maxResultsParamIndex];
            final Integer maxValue = rawMaxValue == null ? null : rawMaxValue.intValue();
            command.max = Objects.firstNonNull(maxValue, -1);
        }
    }

    private Object[] getParams(final Integer[] positions, final Object[] arguments) {
        final Object[] res = new Object[positions.length];
        for (int i = 0; i < positions.length; i++) {
            res[i] = arguments[positions[i]];
        }
        return res;
    }

    private Map<String, Object> getNamedParams(final Map<String, Integer> positions, final Object[] arguments) {
        final Map<String, Object> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            res.put(entry.getKey(), arguments[entry.getValue()]);
        }
        return res;
    }
}
