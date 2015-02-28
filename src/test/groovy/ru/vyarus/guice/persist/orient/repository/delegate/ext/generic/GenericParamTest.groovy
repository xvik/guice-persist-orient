package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.support.GenericRoot
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(RepositoryTestModule)
class GenericParamTest extends AbstractTest {

    @Inject
    GenericRoot repository

    def "Check generic parameter"() {

        when: "call delegate"
        List<Model> res = repository.getAll()
        then: "ok"
        res[0].name == 'getAll'

        when: "call method with special generic interface"
        res = repository.getAll2()
        then: "ok"
        res[0].name == 'getAll2'

        when: "duplicate generic param for the same generic"
        res = repository.duplicateGeneric()
        then: "ok"
        res[0].name == 'duplicateGeneric'

        when: "lookup generic in not present type"
        repository.lookupError()
        then: "fail"
        thrown(RepositoryException)

        when: "lookup not existing generic"
        repository.genericError()
        then: "fail"
        thrown(RepositoryException)

        when: "bad type for generic param"
        repository.genericTypeError()
        then: "fail"
        thrown(RepositoryException)
    }
}