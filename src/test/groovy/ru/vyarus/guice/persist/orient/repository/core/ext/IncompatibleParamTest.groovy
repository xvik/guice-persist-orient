package ru.vyarus.guice.persist.orient.repository.core.ext

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.core.ext.support.IncompatibleParam
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(RepositoryTestModule)
class IncompatibleParamTest extends AbstractTest {

    @Inject
    IncompatibleParam repository

    def "Check param extension compatibility validation"() {

        when: "calling repo method with incompatible parameter extension"
        repository.selectAll(Object.class)
        then: "incompatible param detected"
        thrown(RepositoryException)
    }
}