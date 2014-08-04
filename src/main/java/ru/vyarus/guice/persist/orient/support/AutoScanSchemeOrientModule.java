package ru.vyarus.guice.persist.orient.support;

import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.db.scheme.AutoScanSchemeInitializer;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;

/**
 * Orient shortcut module with predefined "classpath scanning" scheme initializer.
 * Suitable for package by feature approach.
 *
 * @author Vyacheslav Rusakov
 * @since 26.07.2014
 */
public class AutoScanSchemeOrientModule extends OrientModule {

    public AutoScanSchemeOrientModule(final String uri,
                                      final String user,
                                      final String password,
                                      final String basePackage) {
        super(uri, user, password);
        schemeMappingPackage(basePackage);
    }

    @Override
    protected void configurePersistence() {
        super.configurePersistence();
        bind(SchemeInitializer.class).to(AutoScanSchemeInitializer.class);
    }
}
