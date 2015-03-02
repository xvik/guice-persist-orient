package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.RepositoryModule
import ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule
import ru.vyarus.guice.persist.orient.support.Config

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
class CustomModelModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new OrientModule(Config.DB, Config.USER, Config.PASS))
        install(new AutoScanSchemeModule(
                "ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model"))
        install(new RepositoryModule());
    }
}
