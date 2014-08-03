package ru.vyarus.guice.persist.orient;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.persist.finder.DynamicFinder;
import com.google.inject.persist.finder.Finder;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.internal.FinderInterceptor;
import ru.vyarus.guice.persist.orient.finder.internal.FinderInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
public class FinderModule extends AbstractModule {
    private final List<Class<?>> dynamicFinders = Lists.newArrayList();
    private DbType defaultConnectionToUse = DbType.DOCUMENT;

    private FinderInvocationHandler finderInvoker;

    @Override
    protected void configure() {
        bind(DbType.class).annotatedWith(Names.named("orient.finder.default.connection")).toInstance(defaultConnectionToUse);

        finderInvoker = new FinderInvocationHandler();
        requestInjection(finderInvoker);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Finder.class), new FinderInterceptor(finderInvoker));

        // Bind dynamic finders.
        for (Class<?> finder : dynamicFinders) {
            bindFinder(finder);
        }

    }

    public FinderModule useDefaultConnection(DbType connection) {
        this.defaultConnectionToUse = Objects.firstNonNull(connection, defaultConnectionToUse);
        return this;
    }

    /**
     * Adds an interface to this module to use as a dynamic finder.
     *
     * @param iface Any interface type whose methods are all dynamic finders.
     * @param <T> finder type to check resulted proxy
     * @return module instance
     */
    public <T> FinderModule addFinder(Class<T> iface) {
        dynamicFinders.add(iface);
        return this;
    }

    private <T> void bindFinder(Class<T> iface) {
        if (!isDynamicFinderValid(iface)) {
            return;
        }

        @SuppressWarnings("unchecked") // Proxy must produce instance of type given.
                T proxy = (T) Proxy
                .newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iface},
                        finderInvoker);

        bind(iface).toInstance(proxy);
    }

    private boolean isDynamicFinderValid(Class<?> iface) {
        boolean valid = true;
        if (!iface.isInterface()) {
            addError(iface + " is not an interface. Dynamic Finders must be interfaces.");
            valid = false;
        }

        for (Method method : iface.getMethods()) {
            DynamicFinder finder = DynamicFinder.from(method);
            if (null == finder) {
                addError("Dynamic Finder methods must be annotated with @Finder, but " + iface
                        + "." + method.getName() + " was not");
                valid = false;
            }
        }
        return valid;
    }

}
