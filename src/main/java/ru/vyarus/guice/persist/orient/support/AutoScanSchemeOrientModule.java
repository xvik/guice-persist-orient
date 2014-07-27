package ru.vyarus.guice.persist.orient.support;

import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;
import ru.vyarus.guice.persist.orient.db.scheme.autoscan.AutoScanSchemeInitializer;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;

/**
 * Orient shortcut module with predefined "classpath scanning" scheme initializer.
 * Suitable for package by feature approach.
 * <p>NOTE: requires additional dependency on 'reflections' library</p>
 *
 * @author Vyacheslav Rusakov
 * @since 26.07.2014
 */
public class AutoScanSchemeOrientModule extends OrientModule {

    public AutoScanSchemeOrientModule(final String uri,
                                      final String user,
                                      final String password,
                                      final String basePackage) {
        super(uri, user, password, basePackage);
    }

    public AutoScanSchemeOrientModule(final String uri,
                                      final String user,
                                      final String password,
                                      final String basePackage,
                                      final TxConfig txConfig) {
        super(uri, user, password, basePackage, txConfig);
    }

    @Override
    protected void configurePersistence() {
        super.configurePersistence();
        bind(SchemeInitializer.class).to(AutoScanSchemeInitializer.class);
    }
}
