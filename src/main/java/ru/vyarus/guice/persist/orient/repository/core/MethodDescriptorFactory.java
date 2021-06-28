package ru.vyarus.guice.persist.orient.repository.core;

import com.google.common.base.Preconditions;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.executor.ExecutorAnalyzer;
import ru.vyarus.guice.persist.orient.repository.core.executor.RepositoryExecutor;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultAnalyzer;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.java.generics.resolver.GenericsResolver;
import ru.vyarus.java.generics.resolver.context.GenericsInfoFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyze annotated method and provides descriptor. By default resolved descriptor is cached, to avoid
 * possibly heavy computations.
 * <p>
 * Cache may be disabled (e.g. when JRebel used) by using environment variable or system property e.g.:
 * {@code System.setProperty(MethodDescriptorFactory.CACHE_PROPERTY, 'false')}.
 * Property value checked on cache write. To clear current cache state use static method.
 * <p>
 * Note: there is also separate generics parsing cache (generics-resolver). If you will set system or environment
 * property before start or use static method to disable cache then generics cache will be also disabled.
 * If you have problems use static methods to know cache state.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.java.generics.resolver.context.GenericsInfoFactory for generics resolution cache
 * @since 30.07.2014
 */
@Singleton
public class MethodDescriptorFactory {

    /**
     * System property or environment variable name to disable cache.
     * If value is 'false' - cache disabled, otherwise cache enabled.
     */
    public static final String CACHE_PROPERTY = MethodDescriptorFactory.class.getName() + ".cache";
    // lock will not affect performance for cached descriptors, just to make sure nothing was build two times
    private static final ReentrantLock LOCK = new ReentrantLock();

    // cache is not cleared automatically, but it's hard to imagine how many descriptors should be cached
    // to cause problems
    private final Map<String, RepositoryMethodDescriptor> cache = new HashMap<>();

    private final Set<RepositoryExecutor> executors;
    private final RepositoryExecutor defaultExecutor;
    private final SpiService spiService;

    static {
        // if cache disabled with system property (or env variable) it's better to also disable generics
        // cache for simplicity (otherwise one cache could be enabled and second one disabled, which would
        // lead to ambiguous situations)
        if (!isCacheEnabled()) {
            disableCache();
        }
    }

    @Inject
    public MethodDescriptorFactory(final Set<RepositoryExecutor> executors,
                                   @Named("orient.repository.default.connection") final DbType type,
                                   final SpiService spiService) {
        this.executors = executors;
        this.defaultExecutor = Preconditions.checkNotNull(find(type), "No executor found for type " + type);
        this.spiService = spiService;
    }

    public RepositoryMethodDescriptor create(final Method method, final Class<?> type) throws Throwable {
        final String methodIdentity = (type.getName() + " " + method.toString()).intern();
        RepositoryMethodDescriptor descriptor = cache.get(methodIdentity);
        if (descriptor == null) {
            LOCK.lock();
            try {
                if (cache.get(methodIdentity) != null) {
                    // descriptor could be created while thread wait for LOCK
                    descriptor = cache.get(methodIdentity);
                } else {

                    descriptor = buildDescriptor(method, type);
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

    private RepositoryMethodDescriptor buildDescriptor(final Method method, final Class<?> type) {
        final DescriptorContext context = buildDescriptorContext(method, type);
        final RepositoryMethodDescriptor<?> descriptor = spiService.createMethodDescriptor(context);
        descriptor.repositoryRootType = type;
        if (Collection.class.equals(descriptor.returnCollectionHint)) {
            descriptor.returnCollectionHint = null;
        }
        if (descriptor.connectionHint == DbType.UNKNOWN) {
            descriptor.connectionHint = null;
        }
        descriptor.result = ResultAnalyzer.analyzeReturnType(context, descriptor.returnCollectionHint);
        descriptor.executor = ExecutorAnalyzer.analyzeExecutor(descriptor.result, executors,
                defaultExecutor, descriptor.connectionHint, descriptor.resultConversion.customConverter != null);
        return descriptor;
    }

    private DescriptorContext buildDescriptorContext(final Method method, final Class<?> type) {
        final DescriptorContext context = new DescriptorContext();
        context.type = type;
        context.method = method;
        context.generics = GenericsResolver.resolve(type).type(method.getDeclaringClass());
        return context;
    }

    private RepositoryExecutor find(final DbType type) {
        RepositoryExecutor res = null;
        for (RepositoryExecutor executor : executors) {
            if (executor.getType().equals(type)) {
                res = executor;
                break;
            }
        }
        return res;
    }

    /**
     * Clears cached descriptors (already parsed). Also clears parsed generics info
     * ({@link ru.vyarus.java.generics.resolver.context.GenericsInfoFactory}).
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
     * Disables descriptors cache. Also disables parsed generics info cache
     * ({@link ru.vyarus.java.generics.resolver.context.GenericsInfoFactory}).
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
