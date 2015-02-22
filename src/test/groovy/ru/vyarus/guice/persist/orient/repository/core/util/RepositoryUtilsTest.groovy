package ru.vyarus.guice.persist.orient.repository.core.util

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.core.util.support.UsualRepository
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(RepositoryTestModule)
class RepositoryUtilsTest extends AbstractTest {

    @Inject
    UsualRepository repository

    def "Check instance class resolution"() {

        expect: "original class correctly resolved from proxies"
        RepositoryUtils.resolveRepositoryClass(repository) == UsualRepository
    }
}