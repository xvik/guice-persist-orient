package ru.vyarus.guice.persist.orient.support;

import ru.vyarus.guice.persist.orient.db.scheme.impl.PackageSchemeInitializer;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;

/**
 * Shortcut module with predefined "entities in package" scheme initializer.
 * Suitable for package by layer approach.
 * <p>
 * Use it together with main OrientModule.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.db.scheme.impl.PackageSchemeInitializer
 * @since 26.07.2014
 */
public class PackageSchemeModule extends AbstractSchemeModule {

    public PackageSchemeModule(final String... pkg) {
        super(pkg);
    }

    @Override
    protected void bindSchemeInitializer() {
        bind(SchemeInitializer.class).to(PackageSchemeInitializer.class);
    }
}
