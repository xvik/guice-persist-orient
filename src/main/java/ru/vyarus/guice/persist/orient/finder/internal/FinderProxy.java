package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.finder.command.SqlCommandDesc;
import ru.vyarus.guice.persist.orient.finder.placeholder.StringTemplateUtils;
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
    private FinderDescriptorFactory factory;
    @Inject
    private ResultConverter resultConverter;

    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        final Method method = methodInvocation.getMethod();
        final FinderDescriptor descriptor = getFinderDescriptor(method);
        SqlCommandDesc command;
        try {
            command = buildCommand(descriptor, methodInvocation.getArguments());
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format(
                    "Failed to prepare query for finder method %s#%s%s with arguments: %s",
                    method.getDeclaringClass(), method.getName(), Arrays.toString(method.getParameterTypes()),
                    Arrays.toString(methodInvocation.getArguments())), ex);
        }
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

    private FinderDescriptor getFinderDescriptor(final Method method) {
        FinderDescriptor descriptor;
        try {
            descriptor = factory.create(method);
        } catch (Throwable th) {
            throw new IllegalStateException(String.format("Failed to analyze finder method %s#%s%s",
                    method.getDeclaringClass(), method.getName(), Arrays.toString(method.getParameterTypes())), th);
        }
        return descriptor;
    }

    private SqlCommandDesc buildCommand(final FinderDescriptor descriptor, final Object[] arguments) {
        final SqlCommandDesc command = new SqlCommandDesc();
        final String query = processPlaceholders(descriptor, arguments);
        command.isFunctionCall = descriptor.isFunctionCall;
        if (command.isFunctionCall) {
            command.function = query;
        } else {
            command.query = query;
        }
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

    private String processPlaceholders(final FinderDescriptor descriptor, final Object[] arguments) {
        String query = descriptor.isFunctionCall ? descriptor.functionName : descriptor.query;
        if (descriptor.usePlaceholders) {
            query = StringTemplateUtils.replace(query,
                    getPlaceholderParams(descriptor.placeholderParametersIndex, arguments,
                            descriptor.placeholderValues));
        }
        return query;
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

    private Map<String, String> getPlaceholderParams(final Map<String, Integer> positions, final Object[] arguments,
                                                     final Multimap<String, String> defaults) {
        final Map<String, String> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            final String name = entry.getKey();
            final Object value = arguments[entry.getValue()];
            // safeguard from accident null passing
            Preconditions.checkArgument(value != null, "Placeholder '%s' value is null. "
                    + "Use explicit empty string if you need epty replacement for placeholder.", name);
            final String strValue = value.toString();
            // check value with defaults. don't print warning because one warning is enough (during method analysis)
            if (!value.getClass().isEnum() && defaults.containsKey(name)) {
                Preconditions.checkArgument(defaults.get(name).contains(strValue),
                        "Illegal value for placeholder '%s': '%s'", name, strValue);
            }
            res.put(name, strValue);
        }
        return res;
    }
}
