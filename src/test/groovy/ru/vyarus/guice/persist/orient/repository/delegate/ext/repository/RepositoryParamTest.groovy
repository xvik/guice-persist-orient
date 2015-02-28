package ru.vyarus.guice.persist.orient.repository.delegate.ext.repository

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.delegate.ext.repository.support.RepositoryRoot
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(RepositoryTestModule)
class RepositoryParamTest extends AbstractTest {

    @Inject
    RepositoryRoot repository

    def "Check repository param extension"() {

        when: "root repo type"
        List<Model> res = repository.repo()
        then: "ok"
        res[0].name == 'repo'

        when: "subtype"
        res = repository.repoCustom()
        then: "ok"
        res[0].name == 'repoCustom'

        when: "incompatible repo type"
        repository.badType()
        then: "fail"
        thrown(RepositoryException)

        when: "duplicate repo param"
        repository.duplicate()
        then: "fail"
        thrown(RepositoryException)
    }
}