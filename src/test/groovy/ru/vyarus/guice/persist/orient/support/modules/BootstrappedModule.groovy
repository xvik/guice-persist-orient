package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.db.data.DataInitializer
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.PackageSchemeOrientModule
import ru.vyarus.guice.persist.orient.support.service.TestDataInitializer

/**
 * Module with package scheme definition and default data initialization
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
class BootstrappedModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PackageSchemeOrientModule(Config.DB, Config.USER, Config.PASS, Config.MODEL_PKG))
        bind(DataInitializer).to(TestDataInitializer)
    }
}
