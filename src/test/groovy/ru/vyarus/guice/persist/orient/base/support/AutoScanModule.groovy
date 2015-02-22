package ru.vyarus.guice.persist.orient.base.support

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.base.model.support.ModelAuto
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.AutoScanSchemeOrientModule

/**
 * Module with predefined scheme mapping from annotated objects found in classpath.
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class AutoScanModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new AutoScanSchemeOrientModule(Config.DB, Config.USER, Config.PASS, ModelAuto.package.name))
    }
}
