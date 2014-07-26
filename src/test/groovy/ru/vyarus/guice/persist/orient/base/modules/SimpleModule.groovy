package ru.vyarus.guice.persist.orient.base.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.support.PackageSchemeOrientModule

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class SimpleModule extends AbstractModule {

    String pkg;

    SimpleModule() {
        this("ru.vyarus.guice.persist.orient.base.model")
    }

    SimpleModule(pkg) {
        this.pkg = pkg
    }

    @Override
    protected void configure() {
        install(new PackageSchemeOrientModule("memory:test", "admin", "admin", pkg))
    }
}
