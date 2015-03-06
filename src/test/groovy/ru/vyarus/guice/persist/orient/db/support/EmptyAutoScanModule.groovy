package ru.vyarus.guice.persist.orient.db.support

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule
import ru.vyarus.guice.persist.orient.support.Config

/**
 * Module with schema init from classpath scanned objects but with wrong package configured (no objects will be found)
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class EmptyAutoScanModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new OrientModule(Config.DB, Config.USER, Config.PASS))
        install(new AutoScanSchemeModule("wrong.package"))
    }
}
