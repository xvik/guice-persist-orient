package ru.vyarus.guice.persist.orient.db.support

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.db.scheme.support.ModelAuto
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule

/**
 * Module with predefined scheme mapping from annotated objects found in classpath.
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class AutoScanModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new OrientModule(Config.DB, Config.USER, Config.PASS))
        install(new AutoScanSchemeModule(ModelAuto.package.name))
    }
}
