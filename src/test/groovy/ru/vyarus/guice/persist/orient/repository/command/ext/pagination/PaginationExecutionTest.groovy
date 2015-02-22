package ru.vyarus.guice.persist.orient.repository.command.ext.pagination

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
class PaginationExecutionTest extends AbstractTest {

    @Inject
    PaginationCases dao

    def "Check pagination"() {

        setup:
        context.transactionManager.begin()
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe', cnt: 1))
            db.save(new Model(name: 'John', nick: 'Doe', cnt: 2))
            db.save(new Model(name: 'John', nick: 'Doe', cnt: 3))
        } as SpecificTxAction)

        when: "paged select"
        List<Model> res = dao.parametersPaged('John', 'Doe', 0, 1);
        then:
        res.size() == 1
        res[0].cnt == 1

        when: "paged select with shift"
        res = dao.parametersPaged('John', 'Doe', 1, 1);
        then:
        res.size() == 1
        res[0].cnt == 2

        when: "paged select with shift and larger size"
        res = dao.parametersPaged('John', 'Doe', 1, 2);
        then:
        res.size() == 2
        res[0].cnt == 2
        res[1].cnt == 3

        when: "paged select with defaults"
        res = dao.parametersPaged('John', 'Doe', 0, -1);
        then:
        res.size() == 3

        when: "paged select with defaults objects"
        res = dao.parametersPagedObject('John', 'Doe', null, null);
        then:
        res.size() == 3

        cleanup:
        context.transactionManager.end()
    }
}