package ru.vyarus.guice.persist.orient.support;

import com.orientechnologies.orient.core.tx.OTransaction;
import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;
import ru.vyarus.guice.persist.orient.db.scheme.autoscan.AutoScanSchemeInitializer;

/**
 * Orient shortcut module with predefined "classpath scanning" scheme initializer.
 * Suitable for package by feature approach.
 * <p>NOTE: requires additional dependency on 'reflections' library</p>
 *
 * @author Vyacheslav Rusakov
 * @since 26.07.2014
 */
public class AutoScanSchemeOrientModule extends OrientModule {

    public AutoScanSchemeOrientModule(String uri, String user, String password, String basePackage) {
        super(uri, user, password, basePackage);
    }

    public AutoScanSchemeOrientModule(String uri, String user, String password, String basePackage, OTransaction.TXTYPE txtype) {
        super(uri, user, password, basePackage, txtype);
    }

    @Override
    protected void configurePersistence() {
        super.configurePersistence();
        bind(SchemeInitializer.class).to(AutoScanSchemeInitializer.class);
    }
}
