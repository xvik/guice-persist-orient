package ru.vyarus.guice.persist.orient.base.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.ObjectOrientModule
import ru.vyarus.guice.persist.orient.model.ModelInitializer
import ru.vyarus.guice.persist.orient.model.autoscan.AutoscanModelInitializer

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class AutoScanModule extends SimpleModule {

    AutoScanModule() {
    }

    AutoScanModule(pkg) {
        super(pkg)
    }

    @Override
    protected void configure() {
        super.configure()
        bind(ModelInitializer.class).to(AutoscanModelInitializer.class)
    }
}
