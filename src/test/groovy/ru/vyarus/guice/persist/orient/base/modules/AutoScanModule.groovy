package ru.vyarus.guice.persist.orient.base.modules

import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer
import ru.vyarus.guice.persist.orient.db.scheme.autoscan.AutoScanSchemeInitializer
import ru.vyarus.guice.persist.orient.support.AutoScanSchemeOrientModule
import ru.vyarus.guice.persist.orient.support.PackageSchemeOrientModule

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class AutoScanModule extends SimpleModule {

    String pkg;

    AutoScanModule() {
        this("ru.vyarus.guice.persist.orient.base.model")
    }

    AutoScanModule(pkg) {
        this.pkg = pkg
    }

    @Override
    protected void configure() {
        install(new AutoScanSchemeOrientModule("memory:test", "admin", "admin", pkg))
    }
}
