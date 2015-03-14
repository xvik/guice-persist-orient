package ru.vyarus.guice.persist.orient.repository.validation

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import spock.guice.UseModules

import javax.validation.ConstraintViolationException

/**
 * @author Vyacheslav Rusakov 
 * @since 14.03.2015
 */
@UseModules([ValidationModule, BootstrapModule])
class ValidationTest extends AbstractTest {

    @Inject
    ValidationCases repository

    def "Check validation"() {

        when: "using null parameter"
        repository.select(null)
        then: "NotNull constraint reject"
        thrown(ConstraintViolationException)

        when: "empty result"
        repository.select("fdsfsdfd")
        then: "NotEmpty constraint reject"
        thrown(ConstraintViolationException)

        when: "valid case"
        def res = repository.select("name0")
        then: "validation passed"
        res.size() == 1
    }
}