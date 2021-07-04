package ru.vyarus.guice.persist.orient;

import com.google.inject.Binder;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistModule;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;
import ru.vyarus.guice.persist.orient.db.pool.DocumentPool;
import ru.vyarus.guice.persist.orient.db.pool.PoolManager;
import ru.vyarus.guice.persist.orient.db.retry.Retry;
import ru.vyarus.guice.persist.orient.db.retry.RetryMethodInterceptor;
import ru.vyarus.guice.persist.orient.db.scheme.CustomTypesInstaller;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.internal.TransactionInterceptor;
import ru.vyarus.guice.persist.orient.db.OrientDBFactory;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Module provides integration for orient db with guice through guice-persist.
 * <p>
 * Orient storage format is unified within database types (object, document, graph), so it's possible to use
 * the same database as object, document or graph.
 * <p>
 * For example, object connection could be used for schema initialization and property updates
 * and graph connection to work with relations.
 * <p>
 * New orient api (3.0) requires two objects for database connection ({@link OrientDB} created first and used
 * for pool or direct connection creation). {@link OrientDB} instance maybe injected using provider:
 * {@code Provider<OrientDB>}. It is available just after persistence service startup. To get access
 * to module configuration values (related to database url) you can inject {@link OrientDBFactory} bean.
 * <p>
 * Module initialize set of connection pools. By default its object, document and graph
 * (but depends on available jars in classpath:
 * if graph or object jars are not in classpath these pools will not be loaded). Set of pools may be modified
 * by overriding {@code #configurePools()} method.
 * <p>
 * To initialize (create or update) database schema register
 * {@link ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer}
 * implementation. By default no-op implementation registered.
 * <p>
 * Two implementations provided to automatically initialize scheme from domain objects:
 * <ul>
 * <li>{@link ru.vyarus.guice.persist.orient.support.PackageSchemeModule}.
 * Useful if all domain entities located in one package (package by layer)</li>
 * <li>{@link ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule}.
 * Useful if domain model located in different packages or to provide more control on which entities are mapped
 * (package by feature).</li>
 * </ul>
 * NOTE: it's better to not perform db updates in schema initializer, because schema updates
 * must be performed in no-tx mode.
 * <p>
 * Both default initializers use extended object mapper
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer}, build around
 * default orient object mapper. It allows defining custom annotations (plugins).
 * <p>
 * To initialize or migrate database data you can define
 * {@link ru.vyarus.guice.persist.orient.db.data.DataInitializer}. By default,
 * no-op implementation registered. Data initializer called without transaction, because different initialization
 * cases are possible.
 * <p>
 * All pools share the same transaction (object and graph connections use document connection internally).
 * All transactions are orchestrated with {@link ru.vyarus.guice.persist.orient.db.transaction.TransactionManager}.
 * Pool maintains lazy transaction, so when transaction
 * manager starts new transaction, pool will not initialize connection, until connection will be requested.
 * <p>
 * It's possible to override default transaction manager implementation: simply register new manager
 * implementation of {@link ru.vyarus.guice.persist.orient.db.transaction.TransactionManager}.
 * <p>
 * Transaction could be initialized with @Transactional annotation or using transaction templates (
 * {@link ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate} or
 * {@link ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate}. To define transaction type
 * for specific transaction (or switch off transaction within unit of work) use @TxType annotation.
 * Also this could be done with transaction templates.
 * <p>
 * To work with database objects use {@link ru.vyarus.guice.persist.orient.db.PersistentContext}:
 * <ul>
 * <li>PersistentContext&lt;ODatabaseObject&gt; for object db connection</li>
 * <li>PersistentContext&lt;ODatabaseDocument&gt; for document db connection</li>
 * <li>PersistentContext&lt;OrientBaseGraph&gt; for graph db connection (transactional or not)</li>
 * <li>PersistentContext&lt;OrientGraph&gt; for transactional graph db connection
 * (will fail if notx transaction type)</li>
 * <li>PersistentContext&lt;OrientGraphNoTx&gt; for non transactional graph db connection (will provide only
 * for notx transaction type, otherwise fail)</li>
 * </ul>
 * It is also possible to obtain connection by using provider directly {@code Provider<OObjectDatabaseTx>}.
 * Provider will fail to provide connection if unit of work is not defined (using annotation or transactional template)
 * <p>
 * {@link ru.vyarus.guice.persist.orient.db.PersistentContext} combines provider,
 * templates and access to transaction manager (single point to access almost all api).
 * <p>
 * Persistent service must be manually started or stopped: obtain PersistService and call .start() and .stop() when
 * appropriate. This will start/stop all registered pools. Without initialization any try
 * to obtain connection will fail.
 * <p>
 * Local database auto creation is enabled by default. Disable it if required.
 *
 * @see ru.vyarus.guice.persist.orient.db.transaction.TransactionManager for details about transactions
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class OrientModule extends PersistModule {
    private final Logger logger = LoggerFactory.getLogger(OrientModule.class);

    private final String uri;
    private final String user;
    private final String password;
    private String serverUser;
    private String serverPassword;
    private ODatabaseType remoteType;
    private OrientDBConfig config = OrientDBConfig.defaultConfig();
    private TxConfig txConfig;
    private boolean autoCreateLocalDb = true;
    private final Set<Class<? extends OObjectSerializer>> customTypes = new LinkedHashSet<>();

    private Multibinder<PoolManager> poolsMultibinder;
    private MethodInterceptor interceptor;

    /**
     * Configures module with database credentials.
     * Uri consists of full path including database name, for example:
     * <ul>
     * <li>memory:test</li>
     * <li>plocal:./directory/test</li>
     * <li>remote:localhost/test</li>
     * <li>embedded:./directory/test (same as plocal)</li>
     * </ul>
     * Full uri is parsed into main and dbname parts and used for {@link com.orientechnologies.orient.core.db.OrientDB}
     * and connection objects respectively. For example, "plocal:./directory/test" will be parsed and used as
     * <pre>{@code
     *  OrientDB orientDB = new OrientDB("plocal:./directory/");
     *  ODatabaseDocument db = orientDB.open("test","user", "pass");
     * }</pre>
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
        this.autoCreateLocalDb = autoCreateDb;
        return this;
    }

    /**
     * Enable automatic creation of remote databases (if not already exists). When specified,
     * {@link OrientDB} injectable as bean will be created with this credentials: any other bean may inject it and
     * use for remote server manipulations (be cautious!).
     * <p>
     * In tests, remote database creation could be activated indirectly using
     * {@link OrientDBFactory#enableAutoCreationRemoteDatabase(String, String, ODatabaseType)}.
     * NOTE: indirect values will never override direct configuration (so if you specify server user with direct
     * module method, indirect specification will not override it)
     *
     * @param serverUser     remote server user name
     * @param serverPassword remote server user password
     * @param type           target database type (plocal or memory)
     * @return module itself for chained calls
     */
    public OrientModule autoCreateRemoteDatabase(final String serverUser,
                                                 final String serverPassword,
                                                 final ODatabaseType type) {
        this.serverUser = serverUser;
        this.serverPassword = serverPassword;
        this.remoteType = type;
        return this;
    }

    /**
     * Custom orient configuration object, used for {@link OrientDB} instance configuration.
     * When not specified, default ({@code OrientDBConfig.defaultConfig()}) is used.
     * <p>
     * Note that config could override global configuration values (configured directly through
     * {@link com.orientechnologies.orient.core.config.OGlobalConfiguration}) for created database instance.
     *
     * @param config orient configuration instance
     * @return module itself for chained calls
     */
    public OrientModule withConfig(final OrientDBConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Method simplifies binding of custom type serializers (preserving order). Serializers assumed to be guice beans:
     * all passed classes will be bound as guice beans. Method may be called multiple times: each time it will append
     * new serializers.
     * <p>
     * <b>PAY ATTENTION</b> that custom types are global (affect all databases). But, during types registration,
     * exact database url is used to check and remove custom types if they are registered as entities
     * (see {@link OObjectSerializerContext#bind(OObjectSerializer, com.orientechnologies.orient.core.db.ODatabase)}).
     * <p>
     * Serializers are registered with default (null) context
     * ({@code OObjectSerializerHelper.bindSerializerContext(null, serializerContext);}) and so it could be overridden
     * by manual static call (or it could override previous manual configuration). Make sure that only one method used.
     * Null context must be used because it's the only way to delegate type resolution to the context containing all
     * custom serializers and allow you to easily register universal serializers for handling multiple types
     * (for example how serialization lookup is performed:
     * {@link OObjectSerializerHelper#serializeFieldValue(java.lang.Class, java.lang.Object)}).
     * <p>
     * If you use multiple {@link OrientModule}s and register custom types in both then all custom types will be
     * registered because guice multibinder is used to collect all serializer beans. Order of serializers
     * will be preserved inside modules, but the order of modules is not predictable (not restricted by guice)
     * and so it is impossible to order serializers between different modules.
     * <p>
     * All passed serializers are bound to guice context ({@code binder.bind(serializerClass)}). If you need to use
     * complex binding (default mechanism doesn't fit for your needs) then use multibinder directly:
     * {@code Multibinder.newSetBinder(binder(), OObjectSerializer.class).addBinding().to(serializerClass)}.
     * This will not override serializers specified directly in module! (Multibinder.newSetBinder does not mean
     * multibinder context refresh).
     *
     * @param serializers custom type serializers
     * @return module itself for chained calls
     * @see CustomTypesInstaller
     * @see <a href="https://orientdb.org/docs/3.1.x/java/Object-2-Record-Java-Binding.html">custom types</a>
     */
    public OrientModule withCustomTypes(final Class<? extends OObjectSerializer>... serializers) {
        customTypes.addAll(Arrays.asList(serializers));
        return this;
    }

    @Override
    protected void configurePersistence() {
        poolsMultibinder = Multibinder.newSetBinder(binder(), PoolManager.class);

        final OrientDBFactory info = new OrientDBFactory(
                uri, user, password, autoCreateLocalDb, config, serverUser, serverPassword, remoteType);
        bind(OrientDBFactory.class).toInstance(info);
        bind(TxConfig.class).annotatedWith(Names.named("orient.txconfig"))
                .toInstance(txConfig == null ? new TxConfig() : txConfig);

        configureCustomTypes();
        bind(CustomTypesInstaller.class);

        // extension points
        bind(TransactionManager.class);
        // SchemeInitializer.class
        // DataInitializer.class

        bind(PersistService.class).to(DatabaseManager.class);
        bind(OrientDB.class).toProvider(DatabaseManager.class);
        bind(UnitOfWork.class).to(TransactionManager.class);

        configurePools();
        configureInterceptor();
        bindRetryInterceptor();
    }

    /**
     * Binds registered custom types as guice singletons and register them with multibinder.
     */
    private void configureCustomTypes() {
        // empty binding is required in any case
        final Multibinder<OObjectSerializer> typesBinder =
                Multibinder.newSetBinder(binder(), OObjectSerializer.class);
        if (!customTypes.isEmpty()) {
            for (Class<? extends OObjectSerializer> type : customTypes) {
                bind(type).in(Singleton.class);
                typesBinder.addBinding().to(type);
            }
        }
    }

    /**
     * Default pools configuration.
     * Setup object, document and graph pools.
     * Override to register new pool implementations or reduce default pools.
     * NOTE: graph pool requires 3 providers: one for base graph type (OrientBaseGraph) and two more for
     * transactional (OrientGraph) and not transactional (OrientGraphNoTx) connections.
     */
    protected void configurePools() {
        bindPool(ODatabaseDocument.class, DocumentPool.class);

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
     * Use to register custom pools in {@link #configurePools()}.
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
        // hack to correctly bind @Transactional annotation for java8:
        // aop tries to intercept synthetic methods which cause a lot of warnings
        // (and generally not correct)
        super.bindInterceptor(classMatcher, new AbstractMatcher<Method>() {
            @Override
            public boolean matches(final Method method) {
                return !method.isSynthetic() && !method.isBridge() && methodMatcher.matches(method);
            }
        }, interceptors);
    }

    @Override
    protected MethodInterceptor getTransactionInterceptor() {
        return interceptor;
    }

    protected void bindRetryInterceptor() {
        // retry interceptor must be bound before transactional interceptor
        final RetryMethodInterceptor retryInterceptor = new RetryMethodInterceptor();
        requestInjection(retryInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Retry.class), retryInterceptor);
    }
}
