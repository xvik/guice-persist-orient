package ru.vyarus.guice.persist.orient.repository.core.ext

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.ProvisionException
import ru.vyarus.guice.persist.orient.repository.core.ext.support.IllegalDeclaration
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(RepositoryTestModule)
class IllegalMethodDeclarationTest extends Specification {

    @Inject
    Injector injector

    def "Check two extensions declared on method"() {

        when: "trying to create repository instance"
        injector.getInstance(IllegalDeclaration)
        then: "error thrown"
        thrown(ProvisionException)

    }
}