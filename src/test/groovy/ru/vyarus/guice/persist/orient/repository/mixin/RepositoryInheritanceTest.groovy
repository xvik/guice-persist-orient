package ru.vyarus.guice.persist.orient.repository.mixin

import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.mixin.support.BeanPowerRepository
import ru.vyarus.guice.persist.orient.repository.mixin.support.PowerRepository
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
@UseModules(RepositoryTestModule)
class RepositoryInheritanceTest extends AbstractTest {

    @Inject
    PowerRepository repository
    @Inject
    BeanPowerRepository beanRepository

    def "Test repositories inheritance and generification"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "calling generified repository method"
        List<Model> res = repository.selectAll()
        then: "repository detected and executed"
        res.size() == 1

        when: "calling other generified repository method"
        res = repository.findByField('name', 'John')
        then: "repository detected and executed"
        res.size() == 1

        when: "calling repository method with generified return type"
        Model res2 = repository.selectOne()
        then: "repository detected and executed"
        res2

        when: "calling repository method with generified return type"
        com.google.common.base.Optional<Model> res3 = repository.selectOptional()
        then: "repository detected and executed"
        res3.get()

        when: "calling repository method with generified return type"
        Iterator<Model> res4 = repository.selectAllIterator()
        then: "repository detected and executed"
        res4
        res4.next() instanceof Model

        when: "calling repository method with generified return type"
        res = repository.selectAllComplex()
        then: "repository detected and executed"
        res.size() == 1

        when: "calling repository method with generified return type"
        Model[] res5 = repository.selectCustom()
        then: "repository detected and executed"
        res5.length == 1

        when: "calling repository method with generified return type"
        res5 = repository.selectCustomArray()
        then: "repository detected and executed"
        res5.length == 1

        when: "calling repository method with generified return type"
        res = repository.selectCustomList()
        then: "repository detected and executed"
        res.size() == 1
    }

    def "Test bean repositories inheritance and generification"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "calling generified repository method"
        List<Model> res = beanRepository.selectAll()
        then: "repository detected and executed"
        res.size() == 1

        when: "calling other generified repository method"
        res = beanRepository.findByField('name', 'John')
        then: "repository detected and executed"
        res.size() == 1

        when: "calling repository method with generified return type"
        Model res2 = beanRepository.selectOne()
        then: "repository detected and executed"
        res2

        when: "calling repository method with generified return type"
        com.google.common.base.Optional<Model> res3 = beanRepository.selectOptional()
        then: "repository detected and executed"
        res3.get()

        when: "calling repository method with generified return type"
        Iterator<Model> res4 = beanRepository.selectAllIterator()
        then: "repository detected and executed"
        res4
        res4.next() instanceof Model
    }
}