package ru.vyarus.guice.persist.orient;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.persist.finder.DynamicFinder;
import com.google.inject.persist.finder.Finder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.command.CommandBuilder;
import ru.vyarus.guice.persist.orient.finder.executor.DocumentFinderExecutor;
import ru.vyarus.guice.persist.orient.finder.internal.FinderInvocationHandler;
import ru.vyarus.guice.persist.orient.finder.internal.FinderProxy;
import ru.vyarus.guice.persist.orient.finder.result.ResultConverter;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Set;

/**
 * Module provides support for dynamic finders. Must be used together with main orient module.
 * <p>Finders supported on interfaces and beans in context. Interfaces must be manually registered in module
 * (see addFinder method).</p>
 * <p>Named and position parameters may be used. To use named parameters use @Named annotation (defines parameter name).
 * Additional annotation @FirstResult and @MaxResults may be used to define pagination. NOTE: in contrast to jpa, where
 * first result defines first result to take, in orient it defines number of records to skip!
 * (actually difference is just one 1 element,
 * so you may think of it as first result, but counting from 0)</p>
 * <p>Supported return types: any collection, array, single element, Iterator and Iterable.
 * NOTE: document and object connections return lists, so to match other types finder will have to convert results.
 * Graph connection always return iterable implementation.</p>
 * <p>You can define specific collection implementation using annotation parameter.
 * This may be used to get rid of orient internal collections
 * or to use some specific collection (for example, use TreeSet to avoid duplicates and sort results)</p>
 * <p>Finder also may call stored function instead of query execution if namedQuery attribute
 * used (@Finder(namedQuery="")).
 * For functions only @MaxResults is supported, @FirstResult parameter will be ignored.</p>
 * <p>On execution return type will be analyzed to select proper connection type to use. If return type not informative
 * (e.g. for update query or if collection without) default connection will be used.
 * By default document connection used,
 * but could be changed through module configuration. Connection type may be also changed using @Use annotation
 * together with @Finder annotation. Connection type annotation (@Use) will be used only if connection type couldn't be
 * detected according to return type (because otherwise finder will fail on type conversion after query)</p>
 * <p>Actual query execution is performed by registered FinderExecutor implementations. Each implementation provides
 * logic to recognize return type and query execution logic.</p>
 * <p>It is possible to replace default orient command building logic (conversion of query to orient sql command).
 * Simply register new implementation of CommandBuilder: bind(CommandBuilder.class).to(MyCommandBuilder.class)</p>
 * <p>Query result converter may be overridden too: bind(ResultConverter.class).to(MyResultConverter.class)</p>
 * <p>Also, there is predefined AutoScanFinderModule, which is able to find your finders automatically
 * with classpath scanning</p>
 * <p>Based on guice-persist jpa module com.google.inject.persist.jpa.JpaPersistModule</p>
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
public class FinderModule extends AbstractModule {
    private final Logger logger = LoggerFactory.getLogger(FinderModule.class);

    private final Set<Class<?>> dynamicFinders = Sets.newHashSet();
    private DbType defaultConnectionToUse = DbType.DOCUMENT;

    private FinderInvocationHandler finderInvoker;
    private Multibinder<FinderExecutor> executorsMultibinder;

    /**
     * Allows to register all interfaces using constructor, instead of calling addFinder for each interface.
     *
     * @param finderInterface array of finder interfaces
     */
    public FinderModule(final Class<?>... finderInterface) {
        if (finderInterface.length > 0) {
            dynamicFinders.addAll(Arrays.asList(finderInterface));
        }
    }

    /**
     * By default document connection is used.
     *
     * @param connection connection type to use for ambiguous cases (when finder can't recognize it)
     * @return module itself for chained calls
     */
    public FinderModule defaultConnectionType(final DbType connection) {
        this.defaultConnectionToUse = Objects.firstNonNull(connection, defaultConnectionToUse);
        return this;
    }

    /**
     * Adds an interface to this module to use as a dynamic finder.
     *
     * @param iface Any interface type whose methods are all dynamic finders.
     * @param <T>   finder type to check resulted proxy
     * @return module instance
     */
    public <T> FinderModule addFinder(final Class<T> iface) {
        dynamicFinders.add(iface);
        return this;
    }

    @Override
    protected void configure() {
        bind(DbType.class).annotatedWith(Names.named("orient.finder.default.connection"))
                .toInstance(defaultConnectionToUse);

        // extension points
        bind(CommandBuilder.class);
        bind(ResultConverter.class);

        final FinderProxy proxy = new FinderProxy();
        requestInjection(proxy);
        bind(FinderProxy.class).toInstance(proxy);
        finderInvoker = new FinderInvocationHandler();
        requestInjection(finderInvoker);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Finder.class), proxy);

        executorsMultibinder = Multibinder.newSetBinder(binder(), FinderExecutor.class);

        configureExecutors();
        configureFinders();
    }

    /**
     * Configures executors, used to execute queries in different connection types.
     * Override to register different executors.
     */
    protected void configureExecutors() {
        bindExecutor(DocumentFinderExecutor.class);

        loadOptionalExecutor("ru.vyarus.guice.persist.orient.support.finder.ObjectFinderExecutorBinder");
        loadOptionalExecutor("ru.vyarus.guice.persist.orient.support.finder.GraphFinderExecutorBinder");
    }

    /**
     * Installs manually registered finders,
     * Override to register finders from different sources.
     */
    protected void configureFinders() {
        for (Class<?> finder : dynamicFinders) {
            bindFinder(finder);
        }
    }

    /**
     * Register executor for specific connection type.
     *
     * @param executor executor type
     */
    protected void bindExecutor(final Class<? extends FinderExecutor> executor) {
        bind(executor).in(Singleton.class);
        executorsMultibinder.addBinding().to(executor);
    }

    /**
     * Allows to load executor only if required jars are in classpath.
     * For example, no need for graph dependencies if only object db is used.
     *
     * @param executorBinder executor binder class
     * @see ru.vyarus.guice.persist.orient.support.finder.ObjectFinderExecutorBinder as example
     */
    protected void loadOptionalExecutor(final String executorBinder) {
        try {
            final Method bindExecutor = FinderModule.class.getDeclaredMethod("bindExecutor", Class.class);
            bindExecutor.setAccessible(true);
            try {
                Class.forName(executorBinder)
                        .getConstructor(FinderModule.class, Method.class)
                        .newInstance(this, bindExecutor);
            } finally {
                bindExecutor.setAccessible(false);
            }
        } catch (Exception ignore) {
            if (logger.isTraceEnabled()) {
                logger.trace("Failed to process executor loader " + executorBinder, ignore);
            }
        }
    }

    /**
     * @param iface finder interface
     * @param <T>   finder type to check resulted proxy
     */
    protected <T> void bindFinder(final Class<T> iface) {
        if (!isDynamicFinderValid(iface)) {
            return;
        }

        // Proxy must produce instance of type given.
        @SuppressWarnings("unchecked")
        final T proxy = (T) Proxy
                .newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iface},
                        finderInvoker);

        bind(iface).toInstance(proxy);
    }

    private boolean isDynamicFinderValid(final Class<?> iface) {
        boolean valid = true;
        if (!iface.isInterface()) {
            addError(iface + " is not an interface. Dynamic Finders must be interfaces.");
            valid = false;
        }

        for (Method method : iface.getMethods()) {
            final DynamicFinder finder = DynamicFinder.from(method);
            if (null == finder) {
                addError("Dynamic Finder methods must be annotated with @Finder, but " + iface
                        + "." + method.getName() + " was not");
                valid = false;
            }
        }
        return valid;
    }

}
