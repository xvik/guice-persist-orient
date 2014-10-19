package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.FinderModule
import ru.vyarus.guice.persist.orient.support.finder.inheritance.PowerFinder

/**
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
class InheritanceFinderModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PackageSchemeModule())
        install(new FinderModule(PowerFinder))
    }
}
