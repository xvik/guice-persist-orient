package ru.vyarus.guice.persist.orient.support;

import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.db.scheme.PackageSchemeInitializer;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;

/**
 * Orient shortcut module with predefined "entities in package" scheme initializer.
 * Suitable for package by layer approach.
 *
 * @author Vyacheslav Rusakov
 * @since 26.07.2014
 */
public class PackageSchemeOrientModule extends OrientModule {

    public PackageSchemeOrientModule(final String uri,
                                     final String user,
                                     final String password,
                                     final String basePackage) {
        super(uri, user, password);
        schemeMappingPackage(basePackage);
    }

    @Override
    protected void configurePersistence() {
        super.configurePersistence();
        bind(SchemeInitializer.class).to(PackageSchemeInitializer.class);
    }
}
