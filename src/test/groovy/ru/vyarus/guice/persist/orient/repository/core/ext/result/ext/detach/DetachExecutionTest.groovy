package ru.vyarus.guice.persist.orient.repository.core.ext.result.ext.detach

import com.google.inject.Inject
import com.orientechnologies.orient.core.exception.ODatabaseException
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class DetachExecutionTest extends AbstractTest {

    @Inject
    DetachCases repository

    def "Check detach"() {

        when: "check objects can be used outside of transaction"
        List<Model> res = repository.select();
        def model = res[0]
        then: "proxy can be used outside of transaction, but data will be incomplete"
        model != null
        model.class != Model

        when: "trying to access property of proxy outside of transaction"
        model.name
        then: "illegal access"
        thrown(ODatabaseException)

        when: "detach list of objects"
        res = repository.selectDetach()
        then: "detached"
        res.size() == 10
        res[0].class == Model
        res[0].name.startsWith('name')

        when: "detach plain object"
        def res2 = repository.selectPlainDetach()
        then: "detached"
        res2.name.startsWith('name')

        when: "detach array object"
        res2 = repository.selectArrayDetach()
        then: "detached"
        res2.length == 10
        res2[0].name.startsWith('name')

        when: "detach set object"
        res2 = repository.selectSetDetach()
        then: "detached"
        res2.size() == 10
        res2.iterator().next().name.startsWith('name')

        when: "detach iterable object"
        res2 = repository.selectIterableDetach()
        then: "detached"
        res2.iterator().next().name.startsWith('name')

        when: "detach iterator object"
        res2 = repository.selectIteratorDetach()
        then: "detached"
        res2.iterator().next().name.startsWith('name')

        when: "detach custom collection object"
        res2 = repository.selectCustomCollectionDetach()
        then: "detached"
        res2 instanceof LinkedList
        res2.size() == 10
        res2.iterator().next().name.startsWith('name')

        when: "trying to detach pure string"
        res2 = repository.noActualDetach()
        then: "no error, because orient support this case and do nothing"
        res2.startsWith('name')

        when: "using wrong connection"
        repository.detachError()
        then: "error, only object connection allowed"
        thrown(MethodExecutionException)
    }
}