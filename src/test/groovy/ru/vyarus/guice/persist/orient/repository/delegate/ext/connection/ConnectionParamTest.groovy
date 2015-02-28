package ru.vyarus.guice.persist.orient.repository.delegate.ext.connection

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.support.ConnectionRepository
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(RepositoryTestModule)
class ConnectionParamTest extends AbstractTest {

    @Inject
    ConnectionRepository repository

    def "Check connection param extension"() {

        when: "raw connection type assignment"
        List<Model> res = repository.rawConnection()
        then: "ok"
        res[0].name == 'rawConnection'

        when: "subtype of connection assignment"
        res = repository.subtypeMatch()
        then: "ok"
        res[0].name == 'subtypeMatch'

        when: "exact connection type match"
        res = repository.exactConnection()
        then: "ok"
        res[0].name == 'exactConnection'

        when: "actual connection is incompatible with parameter"
        repository.incompatible()
        then: "fail"
        thrown(RepositoryException)

        when: "duplicate connection param not allowed"
        repository.duplicate()
        then: "fail"
        thrown(RepositoryException)

    }
}