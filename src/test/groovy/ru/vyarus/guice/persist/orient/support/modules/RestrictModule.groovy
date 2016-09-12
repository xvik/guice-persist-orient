package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule

class RestrictModule extends AbstractModule {

    @Override
    protected void configure() {
        binder().disableCircularProxies()
        binder().requireExactBindingAnnotations()
    }
}
