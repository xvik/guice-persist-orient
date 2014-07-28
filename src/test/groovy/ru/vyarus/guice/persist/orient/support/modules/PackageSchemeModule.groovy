package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.PackageSchemeOrientModule

/**
 * Module with predefined scheme mapping from objects in package.
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
class PackageSchemeModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PackageSchemeOrientModule(Config.DB, Config.USER, Config.PASS, Config.MODEL_PKG))
    }
}
