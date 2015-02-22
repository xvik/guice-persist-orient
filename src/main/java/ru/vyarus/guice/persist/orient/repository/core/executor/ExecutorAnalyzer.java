package ru.vyarus.guice.persist.orient.repository.core.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;

import java.lang.reflect.Method;
import java.util.Set;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

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
     * Selects appropriate executor for repository method.
     *
     * @param method          method to analyze
     * @param descriptor      result definition
     * @param executors       available executors
     * @param defaultExecutor default executor to use
     * @param connectionHint  expected connection type hint
     * @return selected executor instance
     */
    public static RepositoryExecutor analyzeExecutor(
            final Method method,
            final ResultDescriptor descriptor,
            final Set<RepositoryExecutor> executors,
            final RepositoryExecutor defaultExecutor,
            final DbType connectionHint) {

        // even if connection hint available trying to detect to later check compatibility
        RepositoryExecutor executor = selectByType(descriptor.entityType, executors);
        final DbType requestedType = getRequestedConnectionType(method, executor, connectionHint);


        if (executor == null) {
            executor = requestedType != null ? find(requestedType, executors) : defaultExecutor;
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

    private static RepositoryExecutor selectByType(final Class type, final Set<RepositoryExecutor> executors) {
        RepositoryExecutor executor = null;
        for (RepositoryExecutor support : executors) {
            if (support.accept(type)) {
                executor = support;
                break;
            }
        }
        return executor;
    }

    private static DbType getRequestedConnectionType(final Method method, final RepositoryExecutor executor,
                                                     final DbType connectionHint) {
        // ignore document as default value
        final DbType requestedType = connectionHint == DbType.DOCUMENT ? null : connectionHint;

        // annotation guides just ambiguous cases
        if (executor != null && requestedType != null
                && !executor.getType().equals(requestedType)) {
            LOGGER.warn(
                    "Connection hint {} ignored, because correct execution type recognized from return type "
                            + "in repository method {}#{}",
                    connectionHint, method.getDeclaringClass(), method.getName());
        }
        return requestedType;
    }

    private static RepositoryExecutor find(final DbType type, final Set<RepositoryExecutor> executors) {
        RepositoryExecutor res = null;
        for (RepositoryExecutor executor : executors) {
            if (executor.getType().equals(type)) {
                res = executor;
                break;
            }
        }
        return res;
    }
}
