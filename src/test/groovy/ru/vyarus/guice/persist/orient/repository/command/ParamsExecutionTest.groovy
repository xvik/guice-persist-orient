package ru.vyarus.guice.persist.orient.repository.command

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.support.ParametersCases
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class ParamsExecutionTest extends AbstractTest {

    @Inject
    ParametersCases dao

    def "Check params binding"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "positional params"
        List<Model> res = dao.parametersPositional('John', 'Doe');
        then:
        res.size() == 1

        when: "positional params no result"
        res = dao.parametersPositional('John', 'Doooooe');
        then:
        res.size() == 0

        when: "vararg check"
        res = dao.findWithVararg('Sam', 'Dan', 'John');
        then: "returned empty list as array not supported"
        res.size() == 0

        when: "list check"
        res = dao.findWithList(['Sam', 'Dan', 'John']);
        then: "returned list"
        res.size() == 1

        when: "using parameters in update query"
        context.doInTransaction({ db ->
            db.save(new Model(name: 'To', nick: 'Change'))
        } as SpecificTxAction)
        int affect = dao.updateWithParam('changed', 'To');
        then:
        affect == 1
    }
}