package ru.vyarus.guice.persist.orient.repository.core.executor;

import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;

import java.util.Set;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Executor selection analysis based on return type analysis.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class ExecutorAnalyzer {

    private ExecutorAnalyzer() {
    }

    /**
     * Selects appropriate executor for repository method. Custom converters most likely will cause
     * method return type different from raw object, returned from connection. So in such case
     * detection of connection from return type is impossible.
     * <p>
     * If custom converter registered: always use connection hint if available. Note that result
     * converter could also change connection hint.
     * <p>
     * If no custom converter register then if connection hint contradict with result type
     * analysis throw an error.
     *
     * @param descriptor          result definition
     * @param executors           available executors
     * @param defaultExecutor     default executor to use
     * @param connectionHint      expected connection type hint
     * @param customConverterUsed true when custom converter registered
     * @return selected executor instance
     */
    public static RepositoryExecutor analyzeExecutor(
            final ResultDescriptor descriptor,
            final Set<RepositoryExecutor> executors,
            final RepositoryExecutor defaultExecutor,
            final DbType connectionHint,
            final boolean customConverterUsed) {

        final RepositoryExecutor executorByType = selectByType(descriptor.entityType, executors);
        // storing recognized entity type relation to connection specific object (very helpful hint)
        descriptor.entityDbType = executorByType == null ? DbType.UNKNOWN : executorByType.getType();
        final RepositoryExecutor executor;
        if (connectionHint != null) {
            // connection hint in priority
            executor = find(connectionHint, executors);
            check(executor != null, "Executor not found for type set in annotation %s", connectionHint);
            if (!customConverterUsed) {
                // catch silly errors
                validateHint(executorByType, connectionHint);
            }
        } else {
            // automatic detection
            executor = executorByType == null ? defaultExecutor : executorByType;
        }
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

    private static void validateHint(final RepositoryExecutor executor, final DbType connectionHint) {
        if (executor != null) {
            final DbType autoType = executor.getType();
            if (autoType.equals(DbType.DOCUMENT) && connectionHint.equals(DbType.OBJECT)) {
                // it's ok to use object connection for document selection
                return;
            }
            check(autoType.equals(connectionHint),
                    "Bad connection hint %s specified, when %s expected (according to return type).",
                    connectionHint, autoType);
        }
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
