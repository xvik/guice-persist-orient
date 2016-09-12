package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.support.Config

/**
 * Module with predefined scheme mapping from objects in package.
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
class PackageSchemeModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new OrientModule(Config.DB, Config.USER, Config.PASS))
        install(new ru.vyarus.guice.persist.orient.support.PackageSchemeModule(Config.MODEL_PKG))
        install(new RestrictModule())
    }
}
