package ru.vyarus.guice.persist.orient.repository.command.ext.listen

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.OCommandResultListener
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 27.02.2015
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class ListenExecutionTest extends AbstractTest {

    @Inject
    ListenCases repository

    def "Check listener attaching"() {

        when: "attaching listener"
        def res = []
        repository.select(new OCommandResultListener() {
            int i = 0;

            @Override
            Object getResult() {
                return null
            }

            @Override
            boolean result(Object iRecord) {
                res << iRecord
                // manually filter only 5 first results
                return ++i < 5
            }

            @Override
            void end() {
            }
        })
        then: "5 results selected"
        res.size() == 5
    }

    def "Check error cases"() {

        when: "wrong listener type defined"
        repository.wrongType(null)
        then: "error"
        thrown(RepositoryException)

        when: "duplicate listener definition"
        repository.duplicate(null, null)
        then: "error"
        thrown(RepositoryException)

        when: "listener used with update query"
        repository.updateWithListener(null, null, null)
        then: "error"
        thrown(RepositoryException)

        when: "listener used with not void method"
        repository.returnType({} as OCommandResultListener)
        then: "error"
        thrown(RepositoryException)

        when: "calling with null listener"
        repository.select(null)
        then: "error"
        thrown(RepositoryException)
    }
}