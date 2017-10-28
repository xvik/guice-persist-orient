package ru.vyarus.guice.persist.orient;

import com.google.common.base.MoreObjects;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.ext.core.generator.anchor.GeneratorAnchorModule;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.RepositoryMethodInterceptor;
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException;
import ru.vyarus.guice.persist.orient.repository.core.executor.RepositoryExecutor;
import ru.vyarus.guice.persist.orient.repository.core.executor.impl.DocumentRepositoryExecutor;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.ParamsService;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ResultService;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;
import ru.vyarus.guice.persist.orient.repository.delegate.DelegateMethodExtension;

import javax.inject.Singleton;
import java.lang.reflect.Method;

/**
 * Module provides support for spring-data like repositories. Must be used together with main orient module.
 * <p>
 * It's not limited to repository pattern and may be used to usual daos. Repositories usage is very similar
 * to spring-data (the most popular repository implementation). Annotations intentionally named the same way
 * as in spring-data.
 * <p>
 * In contrast to spring, which use instance proxies, guice aop is tied to class proxies. So aop could be
 * applied to bean only if guice creates it's instance. Repositories implementation completely relies on guice aop.
 * To make it work for interfaces and abstract classes, special class proxy must be generated (to let guice properly
 * instantiate normal class). Implementation of this mechanism is in
 * <a href="https://github.com/xvik/guice-ext-annotations#usage">separate project</a>.
 * <p>
 * Repositories based on plugin architecture, so new types could be easily added and existing types extended.
 * All extensions are annotation driven (annotation must be marked with special annotation, containing
 * extension class). Repository methods are handled with guice aop, bu simply searching for annotated annotations (
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod}). Two more generic types
 * of extensions supported: parameter extensions, when some parameters should get special meaning. And
 * amend extensions, which may be used to amend method call behaviour (e.g. set timeout).
 * <p>
 * Repository method processing starts with method descriptor creation. During descriptor creation, extension
 * could analyze method definition correctness and prepare all required information for fast execution. Generated
 * descriptors are cached to reuse on future calls. In order to process method, extension is called with prepared
 * descriptor.
 * <p>
 * After execution result could be automatically converted (e.g. between collections, arrays, get first element
 * of result list, etc.).
 * See {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter}.
 * <p>
 * Based on guice-persist jpa module {@link com.google.inject.persist.jpa.JpaPersistModule}
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
public class RepositoryModule extends AbstractModule {
    private final Logger logger = LoggerFactory.getLogger(RepositoryModule.class);

    private DbType defaultConnectionToUse = DbType.DOCUMENT;
    private Multibinder<RepositoryExecutor> executorsMultibinder;

    /**
     * By default document connection is used.
     *
     * @param connection connection type to use for ambiguous cases (when it's impossible to recognize)
     * @return module itself for chained calls
     */
    public RepositoryModule defaultConnectionType(final DbType connection) {
        this.defaultConnectionToUse = MoreObjects.firstNonNull(connection, defaultConnectionToUse);
        return this;
    }

    @Override
    protected void configure() {
        install(new GeneratorAnchorModule());
        bind(DbType.class).annotatedWith(Names.named("orient.repository.default.connection"))
                .toInstance(defaultConnectionToUse);

        // extension points
        bind(ResultConverter.class);
        bind(RecordConverter.class);

        // required explicit binding to inject correct injector instance (instead of always root injector)
        bind(SpiService.class);
        bind(AmendExtensionsService.class);
        bind(ParamsService.class);
        bind(ResultService.class);
        bind(DelegateMethodExtension.class);

        configureAop();

        executorsMultibinder = Multibinder.newSetBinder(binder(), RepositoryExecutor.class);

        configureExecutors();
    }

    /**
     * Configures repository annotations interceptor.
     */
    protected void configureAop() {
        final RepositoryMethodInterceptor proxy = new RepositoryMethodInterceptor();
        requestInjection(proxy);
        // repository specific method annotations (query, function, delegate, etc.)
        bindInterceptor(Matchers.any(), new AbstractMatcher<Method>() {
            @Override
            public boolean matches(final Method method) {
                // this will throw error if two or more annotations specified (fail fast)
                try {
                    return ExtUtils.findMethodAnnotation(method) != null;
                } catch (Exception ex) {
                    throw new MethodDefinitionException(String.format("Error declaration on method %s",
                            RepositoryUtils.methodToString(method)), ex);
                }
            }
        }, proxy);
    }

    /**
     * Configures executors, used to execute queries in different connection types.
     * Override to register different executors.
     */
    protected void configureExecutors() {
        bindExecutor(DocumentRepositoryExecutor.class);

        loadOptionalExecutor("ru.vyarus.guice.persist.orient.support.repository.ObjectExecutorBinder");
        loadOptionalExecutor("ru.vyarus.guice.persist.orient.support.repository.GraphExecutorBinder");
    }

    /**
     * Register executor for specific connection type.
     *
     * @param executor executor type
     */
    protected void bindExecutor(final Class<? extends RepositoryExecutor> executor) {
        bind(executor).in(Singleton.class);
        executorsMultibinder.addBinding().to(executor);
    }

    /**
     * Allows to load executor only if required jars are in classpath.
     * For example, no need for graph dependencies if only object db is used.
     *
     * @param executorBinder executor binder class
     * @see ru.vyarus.guice.persist.orient.support.repository.ObjectExecutorBinder as example
     */
    protected void loadOptionalExecutor(final String executorBinder) {
        try {
            final Method bindExecutor = RepositoryModule.class.getDeclaredMethod("bindExecutor", Class.class);
            bindExecutor.setAccessible(true);
            try {
                Class.forName(executorBinder)
                        .getConstructor(RepositoryModule.class, Method.class)
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
}
