package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.CustomModelModule
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.FetchPlanCases
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

        context.doInTransaction({ db ->
            db.save(new Basket(name: 'one', items: [new Item(name: 'test', person: new Person(name: 'john'))]))
        } as SpecificTxAction)

        when: "custom plan"
        Basket res = context.doInTransaction({ db->
            // -1 no limits
            def basket = dao.selectBasket("*:-1")
            assert basket.name == 'one'
            assert basket.items.size() == 1
            basket
        } as SpecificTxAction)
        then: "loaded"
        res

        when: "other custom plan"
        res = context.doInTransaction({ db->
            // do not load anything except object
            def basket = dao.selectBasket("*:-2")
            assert basket.name == 'one'
            assert basket.items.size() == 1
            basket
        } as SpecificTxAction)
        then: "loaded"
        res

        when: "no default plan"
        res = context.doInTransaction({ db->
            // do not load anything except object
            def basket = dao.selectBasketNoDefault(null)
            assert basket.name == 'one'
            assert basket.items.size() == 1
            basket
        } as SpecificTxAction)
        then: "loaded"
        res
    }
}