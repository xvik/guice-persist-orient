package ru.vyarus.guice.persist.orient;

import com.google.inject.Binder;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistModule;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;
import ru.vyarus.guice.persist.orient.db.pool.DocumentPool;
import ru.vyarus.guice.persist.orient.db.pool.PoolManager;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.internal.TransactionInterceptor;

import javax.inject.Singleton;
import java.lang.reflect.Method;

/**
 * <p>Module provides integration for orient db with guice through guice-persist.</p>
 * Orient storage format is unified within database types (object, document, graph), so it's possible to use
 * the same database as object, document or graph.
 * This provides different use-cases:
 * <ul>
 * <li>use object connection for schema initialization and graph connection to work with db</li>
 * <li>use graph connection for complex selects and object db for entities manipulation</li>
 * <li>etc</li>
 * </ul>
 * <p>Module initialize set of connection pools. By default its object, document and graph
 * (but depends on available jars in classpath:
 * if graph or object jars are not in classpath these pools will not be loaded). Set of pools may be modified
 * by overriding {@code #configurePools()} method.</p>
 * To initialize (create or update) database schema register
 * {@code ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer}
 * implementation. By default no-op implementation registered. Two implementations provided to
 * automatically initialize scheme from domain objects:
 * <ul>
 * <li>{@code ru.vyarus.guice.persist.orient.db.scheme.PackageSchemeInitializer}.
 * Useful if all domain entities located in one package</li>
 * <li>{@code ru.vyarus.guice.persist.orient.db.scheme.AutoScanSchemeInitializer}.
 * Useful if domian model located in different packages
 * or to provide more control on which entities are mapped.</li>
 * </ul>
 * There are predefined modules with predefined scheme initializers (must be used together with OrientModule):
 * <ul>
 * <li>{@link ru.vyarus.guice.persist.orient.support.PackageSchemeModule}</li>
 * <li>{@link ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule}</li>
 * </ul>
 * NOTE: it's better to not perform db updates in schema initializer, because schema updates
 * must be performed in no-tx mode.
 * <p>To initialize or migrate database data you can define
 * {@code ru.vyarus.guice.persist.orient.db.data.DataInitializer}. By default,
 * no-op implementation registered.</p>
 * <p>All pools share the same transaction (object and graph connections use document connection internally).
 * All transactions are orchestrated with {@code ru.vyarus.guice.persist.orient.db.transaction.TransactionManager}.
 * Pool maintains lazy transaction, so when transaction
 * manager starts new transaction, pool will not initialize connection, until connection will be requested.</p>
 * <p>It's possible to override default transaction manager implementation: simply register new manager
 * implementation of {@code ru.vyarus.guice.persist.orient.db.transaction.TransactionManager}</p>
 * <p>Transaction could be initialized with @Transactional annotation or using transaction templates (
 * {@code ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate} or
 * {@code ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate}. To define transaction type
 * for specific transaction (or switch off transaction within unit of work) use @TxType annotation.
 * Also this could be done with transaction templates.</p>
 * To obtain database connection in application beans use provider:
 * <ul>
 * <li>Provider&lt;OObjectDatabaseTx&gt; for object db connection</li>
 * <li>Provider&lt;ODatabaseDocumentTx&gt; for document db connection</li>
 * <li>Provider&lt;OrientBaseGraph&gt; for graph db connection (transactional or not)</li>
 * <li>Provider&lt;OrientGraph&gt; for transactional graph db connection (will fail if notx transaction type)</li>
 * <li>Provider&lt;OrientGraphNoTx&gt; for non transactional graph db connection (will provide only
 * for notx transaction type, otherwise fail)</li>
 * </ul>
 * Provider will fail to provide connection if unit of work is not defined (using annotation or transactional template)
 * <p>In most cases use {@link ru.vyarus.guice.persist.orient.db.PersistentContext}, which combines provider,
 * templates and access to transaction manager (single point to access almost all api).</p>
 * <p>Persistent service must be manually started or stopped: obtain PersistService and call .start() and .stop() when
 * appropriate. This will start/stop all registered pools. Without initialization any try
 * to obtain connection will fail.</p>
 * <p>Local database auto creation is enabled by default. Disable it if required.</p>
 *
 * @see ru.vyarus.guice.persist.orient.db.transaction.TransactionManager for details about transactions
 */
public class OrientModule extends PersistModule {
    private final Logger logger = LoggerFactory.getLogger(OrientModule.class);

    private final String uri;
    private final String user;
    private final String password;
    private TxConfig txConfig;
    private boolean autoCreateDb = true;

    private Multibinder<PoolManager> poolsMultibinder;
    private MethodInterceptor interceptor;

    /**
     * Configures module with database credentials.
     *
     * @param uri      database uri
     * @param user     database user
     * @param password database password
     */
    public OrientModule(final String uri, final String user, final String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;
    }

    /**
     * Use if you need to change transactions type globally or define some generic exceptions to rollback
     * handling (see @Transactional annotation)
     * By default, {@code OTransaction.TXTYPE.OPTIMISTIC} transactions enabled and no exceptions defined
     * for rollback (every exception will lead to rollback).
     *
     * @param txConfig default tx config to use for transactions without explicit config definition.
     * @return module itself for chained calls
     */
    public OrientModule defaultTransactionConfig(final TxConfig txConfig) {
        this.txConfig = txConfig;
        return this;
    }

    /**
     * Use to disable auto creation. Auto creation works only for local connection types (plocal, memory).
     * By default, local database auto creation is enabled.
     *
     * @param autoCreateDb true to enable auto creation, false to disable
     * @return module itself for chained calls
     */
    public OrientModule autoCreateLocalDatabase(final boolean autoCreateDb) {
        this.autoCreateDb = autoCreateDb;
        return this;
    }

    @Override
    protected void configurePersistence() {
        poolsMultibinder = Multibinder.newSetBinder(binder(), PoolManager.class);

        bindConstant().annotatedWith(Names.named("orient.uri")).to(uri);
        bindConstant().annotatedWith(Names.named("orient.user")).to(user);
        bindConstant().annotatedWith(Names.named("orient.password")).to(password);
        bindConstant().annotatedWith(Names.named("orient.db.autocreate")).to(autoCreateDb);

        bind(TxConfig.class).annotatedWith(Names.named("orient.txconfig"))
                .toInstance(txConfig == null ? new TxConfig() : txConfig);

        // extension points
        bind(TransactionManager.class);
        // SchemeInitializer.class
        // DataInitializer.class

        bind(PersistService.class).to(DatabaseManager.class);
        bind(UnitOfWork.class).to(TransactionManager.class);

        configurePools();
        configureInterceptor();
    }

    /**
     * Default pools configuration.
     * Setup object, document and graph pools.
     * Override to register new pool implementations or reduce default pools.
     * NOTE: graph pool requires 3 providers: one for base graph type (OrientBaseGraph) and two more for
     * transactional (OrientGraph) and not transactional (OrientGraphNoTx) connections.
     */
    protected void configurePools() {
        bindPool(ODatabaseDocumentTx.class, DocumentPool.class);

        // pools availability should depend on available jars in classpath
        // this way object and graph dependencies are optional
        loadOptionalPool("ru.vyarus.guice.persist.orient.support.pool.ObjectPoolBinder");
        loadOptionalPool("ru.vyarus.guice.persist.orient.support.pool.GraphPoolBinder");
    }

    /**
     * Configures transactional annotation interceptor.
     * Override to register different interceptor implementation.
     */
    protected void configureInterceptor() {
        interceptor = new TransactionInterceptor();
        requestInjection(interceptor);
    }

    /**
     * Register pool within pools set and register provider for specified type.
     * Use to register custom pools in {@code #configurePools()}.
     *
     * @param type connection object type
     * @param pool pool type
     * @param <T>  connection object type
     * @param <P>  pool type
     */
    protected final <T, P extends PoolManager<T>> void bindPool(final Class<T> type, final Class<P> pool) {
        bind(pool).in(Singleton.class);
        poolsMultibinder.addBinding().to(pool);
        bind(type).toProvider(pool);
    }

    /**
     * Allows to load pool only if required jars are in classpath.
     * For example, no need for graph dependencies if only object db is used.
     *
     * @param poolBinder pool binder class
     * @see ru.vyarus.guice.persist.orient.support.pool.ObjectPoolBinder as example
     */
    protected void loadOptionalPool(final String poolBinder) {
        try {
            final Method bindPool = OrientModule.class.getDeclaredMethod("bindPool", Class.class, Class.class);
            bindPool.setAccessible(true);
            try {
                Class.forName(poolBinder)
                        .getConstructor(OrientModule.class, Method.class, Binder.class)
                        .newInstance(this, bindPool, binder());
            } finally {
                bindPool.setAccessible(false);
            }
        } catch (Exception ignored) {
            if (logger.isTraceEnabled()) {
                logger.trace("Failed to process pool loader " + poolBinder, ignored);
            }
        }
    }

    @Override
    protected void bindInterceptor(final Matcher<? super Class<?>> classMatcher,
                                   final Matcher<? super Method> methodMatcher,
                                   final MethodInterceptor... interceptors) {
        if (interceptors.length == 1 && interceptors[0] == getTransactionInterceptor()) {
            // hack to correctly bind @Transactional annotation for java8:
            // aop tries to intercept synthetic methods which cause a lot of warnings
            // (and generally not correct)
            super.bindInterceptor(classMatcher, new AbstractMatcher<Method>() {
                @Override
                public boolean matches(final Method method) {
                    return !method.isSynthetic() && !method.isBridge() && methodMatcher.matches(method);
                }
            }, interceptors);
        } else {
            super.bindInterceptor(classMatcher, methodMatcher, interceptors);
        }
    }

    @Override
    protected MethodInterceptor getTransactionInterceptor() {
        return interceptor;
    }
}
