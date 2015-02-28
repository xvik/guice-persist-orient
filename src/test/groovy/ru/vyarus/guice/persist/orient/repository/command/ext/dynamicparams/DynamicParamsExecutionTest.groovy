package ru.vyarus.guice.persist.orient.repository.command.ext.dynamicparams

import com.google.inject.Inject
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
class DynamicParamsExecutionTest extends AbstractTest {

    @Inject
    DynamicParamsCases repository

    def "Check dynamic params executions"() {

        when: "positional list params"
        def res = repository.positionalList(['name1', 'nick1'])
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'

        when: "positional array params"
        res = repository.positionalArray(['name1', 'nick1'] as String[])
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'

        when: "positional varargs params"
        res = repository.positionalVararg('name1', 'nick1')
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'

        when: "named params"
        res = repository.namedMap([name: 'name1', nick: 'nick1'])
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'

        when: "mix positional params"
        res = repository.mixPositional('name1', 'nick1')
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'

        when: "mix names params"
        res = repository.mixNamed('name1', [nick: 'nick1'])
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'

        when: "calling universal method"
        res = repository.universalus('name = ? and nick = ?', 'name1', 'nick1')
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'

        when: "calling universal method with null args"
        res = repository.universalus('name = \'name1\'', null)
        then: "ok"
        res.size() == 1
        res[0].field('name') == 'name1'
    }

    def "Check error cases"() {

        when: "mix named and positional args"
        repository.mixNamedWithPos('name1', 'nick1')
        then: "error"
        thrown(RepositoryException)

        when: "mix positional and named args"
        repository.mixPosWithNamed('name1', [nick: 'nick1'])
        then: "error"
        thrown(RepositoryException)

        when: "named params with null as key"
        repository.namedMap([(null): 'name1', nick: 'nick1'])
        then: "error"
        thrown(RepositoryException)

        when: "named params with empty string as key"
        repository.namedMap([' ': 'name1', nick: 'nick1'])
        then: "error"
        thrown(RepositoryException)
    }
}