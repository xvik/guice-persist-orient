package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.RepositoryModule

/**
 * @author Vyacheslav Rusakov 
 * @since 15.12.2014
 */
class RepositoryTestModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new PackageSchemeModule())
        install(new RepositoryModule());
    }
}
