package ru.vyarus.guice.persist.orient.finder.internal.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.Use;
import ru.vyarus.guice.persist.orient.finder.internal.result.ResultDescriptor;

import java.lang.reflect.Method;
import java.util.Set;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;

/**
 * Executor selection analysis based on return type analysis.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class ExecutorAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorAnalyzer.class);

    private ExecutorAnalyzer() {
    }

    /**
     * Selects appropriate executor for finder method.
     *
     * @param method          method to analyze
     * @param descriptor      result definition
     * @param executors       available executors
     * @param defaultExecutor default executor to use
     * @return selected executor instance
     */
    public static FinderExecutor analyzeExecutor(
            final Method method,
            final ResultDescriptor descriptor,
            final Set<FinderExecutor> executors,
            final FinderExecutor defaultExecutor) {

        // even if annotation set trying to detect to later check compatibility
        FinderExecutor executor = selectByType(descriptor.entityType, executors);
        // @Use annotation
        final DbType requestedType = getRequestedConnectionType(method, executor);


        if (executor == null) {
            executor = requestedType != null ? find(requestedType, executors) : defaultExecutor;
            // we may still use default connection here, but better fail because it's configuration error
            // (and behaviour will be more predictable)
        } else {
            // special case, sometimes document connection could be overridden, for example:
            // when querying for fields in object connection, documents returned, but still we can use object connection
            if (executor.getType().equals(DbType.DOCUMENT) && requestedType != null) {
                executor = find(requestedType, executors);
            }
        }
        check(executor != null, "Executor not found for type set in @Use annotation " + requestedType);
        return executor;
    }

    private static FinderExecutor selectByType(final Class type, final Set<FinderExecutor> executors) {
        FinderExecutor executor = null;
        for (FinderExecutor support : executors) {
            if (support.accept(type)) {
                executor = support;
                break;
            }
        }
        return executor;
    }

    private static DbType getRequestedConnectionType(final Method method, final FinderExecutor executor) {
        final Use use = method.getAnnotation(Use.class);
        final DbType requestedType = use == null ? null : use.value();

        // annotation guides just ambiguous cases
        if (executor != null && requestedType != null
                && !executor.getType().equals(requestedType)) {
            LOGGER.warn(
                    "@Usa annotation ignored, because correct execution type recognized from return type "
                            + "in finder method {}#{}", method.getDeclaringClass(), method.getName());
        }
        return requestedType;
    }

    private static FinderExecutor find(final DbType type, final Set<FinderExecutor> executors) {
        FinderExecutor res = null;
        for (FinderExecutor executor : executors) {
            if (executor.getType().equals(type)) {
                res = executor;
                break;
            }
        }
        return res;
    }
}
