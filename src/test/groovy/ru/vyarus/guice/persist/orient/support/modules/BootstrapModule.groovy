package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.db.data.DataInitializer
import ru.vyarus.guice.persist.orient.support.service.TestDataInitializer

/**
 * Applies data bootstrapping
 *
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
class BootstrapModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataInitializer).to(TestDataInitializer)
    }
}
