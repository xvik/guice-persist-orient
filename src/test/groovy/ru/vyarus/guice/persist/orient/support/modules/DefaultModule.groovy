package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.OrientModule

/**
 * Default module will not perform any schema updates, no data updates.
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class DefaultModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new OrientModule(Config.DB, Config.USER, Config.PASS))
        install(new RestrictModule())
    }
}
