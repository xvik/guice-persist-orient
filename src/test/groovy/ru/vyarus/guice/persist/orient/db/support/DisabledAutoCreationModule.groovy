package ru.vyarus.guice.persist.orient.db.support

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.support.Config

/**
 * Module target not existent database and auto creation disabled.
 *
 * @author Vyacheslav Rusakov 
 * @since 27.09.2014
 */
class DisabledAutoCreationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new OrientModule('memory:not_existent_for_sure', Config.USER, Config.PASS)
                .autoCreateLocalDatabase(false))
    }
}
