package ru.vyarus.guice.persist.orient.finder

import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.inheritance.BeanPowerFinder
import ru.vyarus.guice.persist.orient.support.finder.inheritance.PowerFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.InheritanceFinderModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
@UseModules(InheritanceFinderModule)
class FinderInheritanceTest extends AbstractTest {

    @Inject
    PowerFinder finder
    @Inject
    BeanPowerFinder beanFinder

    def "Test finders inheritance and generification"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "calling generified finder method"
        List<Model> res = finder.selectAll()
        then: "finder detected and executed"
        res.size() == 1

        when: "calling other generified finder method"
        res = finder.findByField('name', 'John')
        then: "finder detected and executed"
        res.size() == 1

        when: "calling finder method with generified return type"
        Model res2 = finder.selectOne()
        then: "finder detected and executed"
        res2

        when: "calling finder method with generified return type"
        com.google.common.base.Optional<Model> res3 = finder.selectOptional()
        then: "finder detected and executed"
        res3.get()

        when: "calling finder method with generified return type"
        Iterator<Model> res4 = finder.selectAllIterator()
        then: "finder detected and executed"
        res4
        res4.next() instanceof Model

        when: "calling finder method with generified return type"
        res = finder.selectAllComplex()
        then: "finder detected and executed"
        res.size() == 1

        when: "calling finder method with generified return type"
        Model[] res5 = finder.selectCustom()
        then: "finder detected and executed"
        res5.length == 1

        when: "calling finder method with generified return type"
        res5 = finder.selectCustomArray()
        then: "finder detected and executed"
        res5.length == 1

        when: "calling finder method with generified return type"
        res = finder.selectCustomList()
        then: "finder detected and executed"
        res.size() == 1
    }

    def "Test bean finders inheritance and generification"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "calling generified finder method"
        List<Model> res = beanFinder.selectAll()
        then: "finder detected and executed"
        res.size() == 1

        when: "calling other generified finder method"
        res = beanFinder.findByField('name', 'John')
        then: "finder detected and executed"
        res.size() == 1

        when: "calling finder method with generified return type"
        Model res2 = beanFinder.selectOne()
        then: "finder detected and executed"
        res2

        when: "calling finder method with generified return type"
        com.google.common.base.Optional<Model> res3 = beanFinder.selectOptional()
        then: "finder detected and executed"
        res3.get()

        when: "calling finder method with generified return type"
        Iterator<Model> res4 = beanFinder.selectAllIterator()
        then: "finder detected and executed"
        res4
        res4.next() instanceof Model
    }
}