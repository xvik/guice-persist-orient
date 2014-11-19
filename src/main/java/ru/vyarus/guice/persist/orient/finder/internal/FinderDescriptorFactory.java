package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.finder.Finder;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate;
import ru.vyarus.guice.persist.orient.finder.internal.delegate.DelegateUtils;
import ru.vyarus.guice.persist.orient.finder.internal.delegate.FinderDelegateDescriptorFactory;
import ru.vyarus.guice.persist.orient.finder.internal.executor.ExecutorAnalyzer;
import ru.vyarus.guice.persist.orient.finder.internal.query.FinderQueryDescriptorFactory;
import ru.vyarus.guice.persist.orient.finder.internal.result.ResultAnalyzer;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Collection;
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
    private final FinderDelegateDescriptorFactory delegateDescriptorFactory;

    // lock will not affect performance for cached descriptors, just to make sure nothing was build two times
    private final ReentrantLock lock = new ReentrantLock();

    @Inject
    public FinderDescriptorFactory(final Set<FinderExecutor> executors,
                                   @Named("orient.finder.default.connection") final DbType type,
                                   final FinderDelegateDescriptorFactory delegateDescriptorFactory) {
        this.executors = executors;
        this.defaultExecutor = Preconditions.checkNotNull(find(type),
                "No executor found for type " + type);
        this.delegateDescriptorFactory = delegateDescriptorFactory;
    }

    public FinderDescriptor create(final Method method, final GenericsContext generics) throws Throwable {
        FinderDescriptor descriptor = finderCache.get(method);
        if (descriptor == null) {
            lock.lock();
            try {
                if (finderCache.get(method) != null) {
                    // finder could be created while thread wait for lock
                    descriptor = finderCache.get(method);
                } else {

                    descriptor = buildDescriptor(method, generics);
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

    private FinderDescriptor buildDescriptor(final Method method, final GenericsContext generics) {
        final GenericsContext genericsContext = generics.type(method.getDeclaringClass());
        final Finder finderAnnotation = method.getAnnotation(Finder.class);
        final FinderDelegate delegateAnnotation = DelegateUtils.findAnnotation(method);
        check(finderAnnotation == null || delegateAnnotation == null,
                "Both query finder and delegate finder definition annotations found.");

        final boolean isQueryFinder = finderAnnotation != null;
        final Class<?> rootClass = generics.getGenericsInfo().getRootClass();
        final FinderDescriptor descriptor = isQueryFinder
                ? FinderQueryDescriptorFactory.buildDescriptor(method, genericsContext)
                : delegateDescriptorFactory.buildDescriptor(method, genericsContext, rootClass);

        descriptor.finderRootType = rootClass;

        final Class<? extends Collection> returnCollectionType = isQueryFinder
                ? finderAnnotation.returnAs() : delegateAnnotation.returnAs();
        Class<? extends Collection> customCollectionType = null;
        if (!Collection.class.equals(returnCollectionType)) {
            customCollectionType = returnCollectionType;
        }
        descriptor.result = ResultAnalyzer.analyzeReturnType(method, genericsContext, customCollectionType);
        descriptor.executor = ExecutorAnalyzer.analyzeExecutor(method, descriptor.result, executors, defaultExecutor);
        return descriptor;
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
