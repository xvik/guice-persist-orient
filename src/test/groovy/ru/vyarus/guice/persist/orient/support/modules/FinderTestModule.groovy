package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.FinderModule
import ru.vyarus.guice.persist.orient.support.AutoScanFinderModule

/**
 * @author Vyacheslav Rusakov 
 * @since 15.12.2014
 */
class FinderTestModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new PackageSchemeModule())
        install(new FinderModule());
    }
}
