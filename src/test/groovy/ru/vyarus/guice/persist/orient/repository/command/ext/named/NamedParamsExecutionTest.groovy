package ru.vyarus.guice.persist.orient.repository.command.ext.named

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class NamedParamsExecutionTest extends AbstractTest {

    @Inject
    NamedParamsCases dao

    def "Check params binding"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "named params"
        List<Model> res = dao.parametersNamed('John', 'Doe');
        then:
        res.size() == 1

        when: "named params no results"
        res = dao.parametersNamed('John', 'Doooooe');
        then:
        res.size() == 0

        when: "positional params with warning"
        res = dao.parametersPositionalWithOrdinal('John', 'Doe');
        then: "ordinal and named params can't be used together"
        thrown(IllegalStateException)

        when: "named params wrong declaration"
        res = dao.parametersNames('John', 'Doe');
        then: "error"
        thrown(IllegalStateException)

        when: "named params with duplicate"
        res = dao.parametersNamesDuplicateName('John', 'Doe');
        then: "error"
        thrown(IllegalStateException)
    }
}