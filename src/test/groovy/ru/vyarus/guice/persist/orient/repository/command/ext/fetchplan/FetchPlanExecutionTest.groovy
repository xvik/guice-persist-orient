package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.CustomModelModule
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.FetchPlanCases
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.ext.CheckCommandExtension
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model.Basket
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model.Item
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model.Person
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@UseModules(CustomModelModule)
class FetchPlanExecutionTest extends AbstractTest {

    @Inject
    FetchPlanCases dao

    def "Check fetchplan binding"() {

        setup:
        context.doInTransaction({ db ->
            db.save(new Basket(name: 'one', items: [new Item(name: 'test', person: new Person(name: 'john'))]))
        } as SpecificTxAction)
        // use long transaction, because object proxy and document doesn't work outside of transaction scope
        context.getTransactionManager().begin()

        when: "checking that expected plan check works"
        dao.selectBasket("*:-1")
        then: 'check failed'
        thrown(RepositoryException)

        when: "custom plan"
        CheckCommandExtension.expectedPlan = "*:-1"
        // -1 no limits
        def basket = dao.selectBasket("*:-1")
        then: "loaded"
        basket.name == 'one'
        basket.items.size() == 1

        when: "other custom plan"
        CheckCommandExtension.expectedPlan = "*:-2"
        // do not load anything except object
        basket = dao.selectBasket("*:-2")
        then: "loaded"
        basket.name == 'one'
        basket.items.size() == 1

        when: "no default plan"
        CheckCommandExtension.expectedPlan = null
        // do not load anything except object
        basket = dao.selectBasketNoDefault(null)
        then: "loaded"
        basket.name == 'one'
        basket.items.size() == 1

        cleanup:
        context.transactionManager.end()
    }
}