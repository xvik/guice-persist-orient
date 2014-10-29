package ru.vyarus.guice.persist.orient.finder.internal.query;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import ru.vyarus.guice.persist.orient.finder.command.SqlCommandDesc;
import ru.vyarus.guice.persist.orient.finder.internal.FinderExecutionException;
import ru.vyarus.guice.persist.orient.finder.placeholder.StringTemplateUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Sql finder invocation logic.
 *
 * @author Vyacheslav Rusakov
 * @since 26.10.2014
 */
public final class QueryInvocation {

    private QueryInvocation() {
    }

    public static Object processQuery(final FinderQueryDescriptor descriptor,
                                      final Method method,
                                      final Object[] arguments) throws Throwable {
        SqlCommandDesc command;
        try {
            command = buildCommand(descriptor, arguments);
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format(
                    "Failed to prepare query for finder method %s#%s%s with arguments: %s",
                    descriptor.finderRootType, method.getName(), Arrays.toString(method.getParameterTypes()),
                    Arrays.toString(arguments)), ex);
        }
        try {
            return descriptor.executor.executeQuery(command);
        } catch (Throwable th) {
            throw new FinderExecutionException(String.format(
                    "Failed to execute query '%s' with parameters %s of finder %s#%s%s",
                    command.query, Arrays.toString(arguments), descriptor.finderRootType,
                    method.getName(), Arrays.toString(method.getParameterTypes())
            ), th);
        }
    }

    private static SqlCommandDesc buildCommand(final FinderQueryDescriptor descriptor, final Object[] arguments) {
        final SqlCommandDesc command = new SqlCommandDesc();
        final String query = processPlaceholders(descriptor, arguments);
        command.isFunctionCall = descriptor.isFunctionCall;
        command.query = query;
        command.useNamedParams = descriptor.params.useNamedParameters;
        if (command.useNamedParams) {
            command.namedParams = getNamedParams(descriptor.params.namedParametersIndex, arguments);
        } else {
            command.params = getParams(descriptor.params.parametersIndex, arguments);
        }

        switch (descriptor.result.returnType) {
            case COLLECTION:
            case ARRAY:
                assignPaginationParams(command, descriptor, arguments);
                break;
            case PLAIN:
                // implicitly limit query for single return result (for performance)
                command.max = 1;
                break;
            default:
                throw new IllegalStateException("Unsupported return type " + descriptor.result.returnType);
        }
        return command;
    }

    private static String processPlaceholders(final FinderQueryDescriptor descriptor, final Object[] arguments) {
        String query = descriptor.query;
        if (descriptor.placeholders != null) {
            final Map<String, String> params = Maps.newHashMap();
            if (descriptor.placeholders.genericParameters != null) {
                params.putAll(descriptor.placeholders.genericParameters);
            }
            if (descriptor.placeholders.parametersIndex != null) {
                params.putAll(
                        getPlaceholderParams(descriptor.placeholders.parametersIndex, arguments,
                                descriptor.placeholders.values)
                );
            }
            query = StringTemplateUtils.replace(query, params);
        }
        return query;
    }

    private static void assignPaginationParams(final SqlCommandDesc command,
                                               final FinderQueryDescriptor descriptor, final Object[] arguments) {
        if (descriptor.pagination != null) {
            if (descriptor.pagination.firstResultParamIndex != null) {
                final Number rawStartValue = (Number) arguments[descriptor.pagination.firstResultParamIndex];
                final Integer startValue = rawStartValue == null ? null : rawStartValue.intValue();
                command.start = Objects.firstNonNull(startValue, 0);
            }

            if (descriptor.pagination.maxResultsParamIndex != null) {
                final Number rawMaxValue = (Number) arguments[descriptor.pagination.maxResultsParamIndex];
                final Integer maxValue = rawMaxValue == null ? null : rawMaxValue.intValue();
                command.max = Objects.firstNonNull(maxValue, -1);
            }
        }
    }

    private static Object[] getParams(final Integer[] positions, final Object[] arguments) {
        final Object[] res = new Object[positions.length];
        for (int i = 0; i < positions.length; i++) {
            res[i] = arguments[positions[i]];
        }
        return res;
    }

    private static Map<String, Object> getNamedParams(final Map<String, Integer> positions, final Object[] arguments) {
        final Map<String, Object> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            res.put(entry.getKey(), arguments[entry.getValue()]);
        }
        return res;
    }

    private static Map<String, String> getPlaceholderParams(final Map<String, Integer> positions,
                                                            final Object[] arguments,
                                                            final Multimap<String, String> defaults) {
        final Map<String, String> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            final String name = entry.getKey();
            final Object value = arguments[entry.getValue()];
            // safeguard from accident null passing
            Preconditions.checkArgument(value != null, "Placeholder '%s' value is null. "
                    + "Use explicit empty string if you need empty replacement for placeholder.", name);
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
