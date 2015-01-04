package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Preconditions;
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
import ru.vyarus.java.generics.resolver.context.GenericsInfoFactory;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;

/**
 * Analyze annotated method and provides descriptor.
 * <p>Cache may be disabled (e.g. when JRebel used) by using environment variable or system property e.g.:
 * {@code System.setProperty(FinderDescriptorFactory.CACHE_PROPERTY, 'false')}.
 * Property value checked on cache write. To clear current cache state use static method.</p>
 * <p>Note: there is also separate generics parsing cache (generics-resolver). If you will set system or environment
 * property before start or use static method to disable cache then generics cache will be also disabled.
 * If you have problems use static methods to know cache state.</p>
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.java.generics.resolver.context.GenericsInfoFactory for generics resolution cache
 * @since 30.07.2014
 */
@Singleton
public class FinderDescriptorFactory {

    /**
     * System property or environment variable name to disable cache.
     * If value is 'false' - cache disabled, otherwise cache enabled.
     */
    public static final String CACHE_PROPERTY = FinderDescriptorFactory.class.getName() + ".cache";
    // lock will not affect performance for cached descriptors, just to make sure nothing was build two times
    private static final ReentrantLock LOCK = new ReentrantLock();

    // cache is not cleared automatically, but it's hard to imagine how many descriptors should be cached
    // to cause problems
    private final Map<String, FinderDescriptor> cache = new HashMap<String, FinderDescriptor>();

    private final Set<FinderExecutor> executors;
    private final FinderExecutor defaultExecutor;
    private final FinderDelegateDescriptorFactory delegateDescriptorFactory;

    static {
        // if finders cache disabled with system property (or env variable) it's better to also disable generics
        // cache for simplicity (otherwise one cache could be enabled and second one disabled, which would
        // lead to ambiguous situations)
        if (!isCacheEnabled()) {
            disableCache();
        }
    }

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
        final String methodIdentity = (generics.getGenericsInfo().getRootClass().getName()
                + " " + method.toString()).intern();
        FinderDescriptor descriptor = cache.get(methodIdentity);
        if (descriptor == null) {
            LOCK.lock();
            try {
                if (cache.get(methodIdentity) != null) {
                    // finder could be created while thread wait for LOCK
                    descriptor = cache.get(methodIdentity);
                } else {

                    descriptor = buildDescriptor(method, generics);
                    if (isCacheEnabled()) {
                        // internal check
                        Preconditions.checkState(cache.get(methodIdentity) == null,
                                "Bad concurrency: descriptor already present in cache");
                        cache.put(methodIdentity, descriptor);
                    }
                }
            } finally {
                LOCK.unlock();
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

    /**
     * Clears cached finder descriptors (already parsed). Also clears parsed generics info
     * ({@see ru.vyarus.java.generics.resolver.context.GenericsInfoFactory}).
     * Cache could be completely disabled using system property or environment variable
     *
     * @see #CACHE_PROPERTY
     */
    public void clearCache() {
        LOCK.lock();
        try {
            cache.clear();
            GenericsInfoFactory.clearCache();
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Disables finder descriptors cache. Also disables parsed generics info cache
     * ({@see ru.vyarus.java.generics.resolver.context.GenericsInfoFactory}).
     */
    public static void disableCache() {
        System.setProperty(CACHE_PROPERTY, Boolean.FALSE.toString());
        GenericsInfoFactory.disableCache();
    }

    /**
     * @return true is cache enabled, false otherwise
     */
    public static boolean isCacheEnabled() {
        final String no = Boolean.FALSE.toString();
        return !no.equals(System.getenv(CACHE_PROPERTY))
                && !no.equals(System.getProperty(CACHE_PROPERTY));
    }
}
