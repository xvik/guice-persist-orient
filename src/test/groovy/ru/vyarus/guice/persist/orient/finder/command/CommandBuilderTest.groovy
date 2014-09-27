package ru.vyarus.guice.persist.orient.finder.command

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.script.OCommandFunction
import com.orientechnologies.orient.core.sql.OCommandSQL
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.support.modules.TestFinderModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 04.08.2014
 */
@UseModules(TestFinderModule)
class CommandBuilderTest extends AbstractTest {

    @Inject
    CommandBuilder builder

    def "Check function recognition"() {

        when: "function call description provided"
        OCommandFunction req = builder.buildCommand(new SqlCommandDesc(
                isFunctionCall: true, query: "test"))
        then: "function query created"
        req.text == "test"

        when: "function with positional params"
        req = builder.buildCommand(new SqlCommandDesc(
                isFunctionCall: true, query: "test", start: 1, max: 10))
        then: "function query created"
        req.text == "test"
        req.limit == 10
    }

    def "Check query recognition"() {

        when: "simple query call"
        OSQLSynchQuery req = builder.buildCommand(new SqlCommandDesc(
                query: "select from Model"))
        then: "query created"
        req.text == "select from Model"

        when: "paginated query call"
        req = builder.buildCommand(new SqlCommandDesc(
                query: "select from Model", start: 10, max: 20))
        then: "query created"
        req.text == "select from Model SKIP 10"
        req.limit == 20
    }

    def "Check update recognition"() {

        when: "simple update call"
        OCommandSQL req = builder.buildCommand(new SqlCommandDesc(
                query: "update Model set name='tst'"))
        then: "command created"
        req.text == "update Model set name='tst'"

        when: "paginated update call"
        req = builder.buildCommand(new SqlCommandDesc(
                query: "update Model set name='tst'", start: 10, max: 20))
        then: "command created, paging ignored"
        req.text == "update Model set name='tst'"
        req.limit == -1

        when: "simple insert call"
        req = builder.buildCommand(new SqlCommandDesc(
                query: "insert into Model ..."))
        then: "command created"
        req.text == "insert into Model ..."

        when: "paginated update call"
        req = builder.buildCommand(new SqlCommandDesc(
                query: "insert into Model ...", start: 10, max: 20))
        then: "command created, paging ignored"
        req.text == "insert into Model ..."
        req.limit == -1
    }
}