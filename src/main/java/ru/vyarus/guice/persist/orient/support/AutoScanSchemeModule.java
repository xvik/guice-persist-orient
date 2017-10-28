package ru.vyarus.guice.persist.orient.support;

import ru.vyarus.guice.persist.orient.db.scheme.impl.AutoScanSchemeInitializer;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;

/**
 * Shortcut module with predefined "classpath scanning" scheme initializer.
 * Suitable for package by feature approach.
 * <p>
 * Use it together with main OrientModule.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.db.scheme.impl.AutoScanSchemeInitializer
 * @since 26.07.2014
 */
public class AutoScanSchemeModule extends AbstractSchemeModule {

    public AutoScanSchemeModule(final String... pkg) {
        super(pkg);
    }

    @Override
    protected void bindSchemeInitializer() {
        bind(SchemeInitializer.class).to(AutoScanSchemeInitializer.class);
    }
}
