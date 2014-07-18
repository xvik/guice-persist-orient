package ru.vyarus.guice.persist.orient.base.modules

import com.google.inject.AbstractModule

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class EmptySimpleModule extends SimpleModule {

    EmptySimpleModule() {
        super("wrong.package")
    }
}
