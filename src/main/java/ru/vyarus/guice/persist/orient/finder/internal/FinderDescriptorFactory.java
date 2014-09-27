package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.finder.Finder;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.internal.executor.ExecutorAnalyzer;
import ru.vyarus.guice.persist.orient.finder.internal.pagination.PaginationAnalyzer;
import ru.vyarus.guice.persist.orient.finder.internal.params.ParamsAnalyzer;
import ru.vyarus.guice.persist.orient.finder.internal.placeholder.PlaceholderAnalyzer;
import ru.vyarus.guice.persist.orient.finder.internal.result.ResultAnalyzer;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;

/**
 * Analyze annotated method and provides descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class FinderDescriptorFactory {

    private final Map<Method, FinderDescriptor> finderCache = new MapMaker().weakKeys().makeMap();

    private final Set<FinderExecutor> executors;
    private final FinderExecutor defaultExecutor;

    // lock will not affect performance for cached descriptors, just to make sure nothing was build two times
    private final ReentrantLock lock = new ReentrantLock();

    @Inject
    public FinderDescriptorFactory(final Set<FinderExecutor> executors,
                                   @Named("orient.finder.default.connection") final DbType type) {
        this.executors = executors;
        this.defaultExecutor = Preconditions.checkNotNull(find(type),
                "No executor found for type " + type);
    }

    public FinderDescriptor create(final Method method) throws Throwable {
        FinderDescriptor descriptor = finderCache.get(method);
        if (descriptor == null) {
            lock.lock();
            try {
                if (finderCache.get(method) != null) {
                    // finder could be created while thread wait for lock
                    descriptor = finderCache.get(method);
                } else {

                    descriptor = buildDescriptor(method);
                    // internal check
                    Preconditions.checkState(finderCache.get(method) == null,
                            "Bad concurrency: descriptor already present in cache");
                    finderCache.put(method, descriptor);
                }
            } finally {
                lock.unlock();
            }
        }
        return descriptor;
    }

    private FinderDescriptor buildDescriptor(final Method method) {
        final Finder finderAnnotation = method.getAnnotation(Finder.class);

        final String functionName = Strings.emptyToNull(finderAnnotation.namedQuery());
        final String query = Strings.emptyToNull(finderAnnotation.query());
        check(Strings.isNullOrEmpty(functionName) || Strings.isNullOrEmpty(query),
                "Choose what to use function or query, but not both");

        final Class<? extends Collection> returnCollectionType = finderAnnotation.returnAs();

        final FinderDescriptor descriptor = new FinderDescriptor();
        descriptor.isFunctionCall = functionName != null;
        descriptor.query = descriptor.isFunctionCall ? functionName : query;

        descriptor.placeholders = PlaceholderAnalyzer.analyzePlaceholders(method, descriptor.query);
        Class<? extends Collection> customCollectionType = null;
        if (!Collection.class.equals(returnCollectionType)) {
            customCollectionType = returnCollectionType;
        }
        descriptor.result = ResultAnalyzer.analyzeReturnType(method, customCollectionType);
        descriptor.executor = ExecutorAnalyzer.analyzeExecutor(method, descriptor.result, executors, defaultExecutor);
        analyzeParameters(method, descriptor);
        return descriptor;
    }

    private void analyzeParameters(final Method method, final FinderDescriptor descriptor) {
        final List<Integer> skip = Lists.newArrayList();
        // recognize pagination annotations
        descriptor.pagination = PaginationAnalyzer.analyzePaginationParameters(method);
        if (descriptor.pagination != null) {
            skip.addAll(descriptor.pagination.getBoundIndexes());
        }
        // recognize placeholder annotations
        PlaceholderAnalyzer.analyzePlaceholderParameters(method, descriptor.placeholders,
                descriptor.query, skip);
        if (descriptor.placeholders != null) {
            skip.addAll(descriptor.placeholders.getBoundIndexes());
        }
        // map all remaining parameters
        descriptor.params = ParamsAnalyzer.analyzeParameters(method, skip);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private FinderExecutor find(final DbType type) {
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
