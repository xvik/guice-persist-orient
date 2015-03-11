package ru.vyarus.guice.persist.orient.repository.command.query

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 11.03.2015
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class QueryExecutionTest extends AbstractTest {

    @Inject
    QueryCases repository

    def "Check queries execution"() {

        when: "select query executed"
        def res = repository.select()
        then: "ok"
        res.size() == 10

        when: "update query executed"
        res = repository.update("test", "name0")
        then: "ok"
        res == 1

        when: "insert query executed"
        res = repository.insert("insert")
        then: "ok"
        res instanceof Model
        res.name == "insert"
    }
}