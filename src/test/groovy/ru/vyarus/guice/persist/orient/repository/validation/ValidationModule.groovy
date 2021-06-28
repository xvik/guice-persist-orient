package ru.vyarus.guice.persist.orient.repository.validation

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule

/**
 * @author Vyacheslav Rusakov 
 * @since 14.03.2015
 */
class ValidationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ru.vyarus.guice.validator.ValidationModule())
        install(new RepositoryTestModule())
    }
}
