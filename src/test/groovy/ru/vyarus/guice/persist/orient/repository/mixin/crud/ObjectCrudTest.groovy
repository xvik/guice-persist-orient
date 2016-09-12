package ru.vyarus.guice.persist.orient.repository.mixin.crud

import com.google.common.collect.Lists
import com.google.inject.Inject
import com.orientechnologies.orient.core.exception.ODatabaseException
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.mixin.crud.support.ObjectDao
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import ru.vyarus.guice.persist.orient.util.transactional.TransactionalTest
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 26.10.2014
 */
@UseModules(RepositoryTestModule)
class ObjectCrudTest extends AbstractTest {

    @Inject
    ObjectDao objectDao

    def "Check object crud mixin"() {

        // need transaction for entire test to work with objects inside transaction
        setup:
        context.transactionManager.begin()

        when: "storing object"
        Model model = new Model(name: 'name', nick: 'tst')
        model = objectDao.save(model)
        then: "object stored"
        model.id

        when: "selecting all records"
        List<Model> all = Lists.newArrayList(objectDao.getAll() as Iterator)
        then: "retrieved"
        all.size() == 1
        all[0] instanceof Model

        when: "selecting all records as list"
        all = objectDao.getAllAsList()
        then: "retrieved"
        all.size() == 1
        all[0] instanceof Model

        when: "loading object"
        model = objectDao.get(model.id)
        then: "object found"
        model

        when: "loading object 2"
        model = objectDao.get(new ORecordId(model.id))
        then: "object found"
        model

        when: "updating object"
        model.name = 'name2'
        model = objectDao.save(model)
        model = objectDao.get(model.id)
        then: "updated object stored"
        model.name == 'name2'
        model.class != Model.class

        when: "detaching object"
        model = objectDao.detach(model)
        then: "object unproxied"
        model.class == Model.class

        when: "attaching object"
        objectDao.attach(model)
        model.name = 'name4'
        model = objectDao.save(model)
        model = objectDao.get(model.id)
        then:
        model.name == 'name4'

        when: "deleting object"
        objectDao.delete(model)
        model = objectDao.get(model.id)
        then: "object removed"
        model == null

        when: "creating proxy and removing object by id"
        model = objectDao.create()
        model.name = 'sample'
        objectDao.save(model)
        objectDao.delete(model.id)
        model = objectDao.get(model.id)
        then: "object removed"
        model == null

        when: "creating proxy and removing object by orid"
        model = objectDao.create()
        model.name = 'sample'
        objectDao.save(model)
        objectDao.delete(new ORecordId(model.id))
        model = objectDao.get(model.id)
        then: "object removed"
        model == null

        cleanup:
        context.transactionManager.end()
    }

    def "Check object advanced detaching"() {

        setup:
        context.transactionManager.begin()
        10.times({
            objectDao.save(new Model(name: 'name' + it, nick: 'tst' + it))
        })

        when: "detaching array"
        Model model = objectDao.findByName('name0')
        Model model2 = objectDao.findByName('name1')
        List<Model> res = objectDao.detachAll(model, model2)
        then: "detached"
        res[0] instanceof Model
        res[0].name == 'name0'
        res[1] instanceof Model
        res[1].name == 'name1'

        when: "detaching iterator"
        res = objectDao.detachAll(objectDao.getAll() as Iterator)
        then: "detached"
        res[0] instanceof Model
        res[0].name == 'name0'
        res[1] instanceof Model
        res[1].name == 'name1'

        when: "detaching iteratable"
        res = objectDao.detachAll(objectDao.getAll(0, 2))
        then: "detached"
        res[0] instanceof Model
        res[0].name == 'name0'
        res[1] instanceof Model
        res[1].name == 'name1'

        cleanup:
        context.transactionManager.end()
    }

    def "Check object attach/detach behaviour"() {

        when: "trying to detach proxy out of transaction"
        Model res = objectDao.save(new Model(name: 'detach', nick: 'detach'));
        res = objectDao.detach(res)
        then: "object detached outside of transaction"
        res.class == Model
        res.name == 'detach'
        res.nick == 'detach'

        when: "modifying and saving object"
        res.setName('detach2')
        res = objectDao.save(res)
        res = objectDao.detach(res)
        then: "object was correctly saved"
        res.class == Model
        res.name == 'detach2'
        res.nick == 'detach'

        when: "trying to attach object and change it"
        res = objectDao.attach(res)
        res.setName('detach3')
        then: "proxy can't be used outside of transaction"
        thrown(ODatabaseException)

        when: "trying to use iterator"
        Iterator<Model> iterator = objectDao.getAll()
        iterator.next()
        then: "db iterator is used outside of transaction"
        thrown(ODatabaseException)
    }

    def "Check object custom mixin"() {

        when: "check generic pass and repository instance"
        Object res = objectDao.doSomething(1, 2)
        then: "called"
        res == null

        when: "check generic pass and connection object"
        res = objectDao.doSomething2(1, 2, Object)
        then: "called"
        res == null

        when: "check incorrect connection type"
        objectDao.badCall()
        then: "bad connection param"
        thrown(RepositoryException)

        when: "check graph connection type, recognized with annotation"
        objectDao.graphCall()
        then: "bad connection param"
        true

        when: "invocation fails"
        objectDao.invocationFail()
        then: "fail"
        thrown(RepositoryException)

        when: "specific selection"
        objectDao.paramSpecific(1, 1, 'hjh')
        then: "correct specific method chosen"
        true
    }

    @TransactionalTest
    def "Check object conversions"() {

        when: "converting not persisted pojo"
        Model model = new Model(name: 'test')
        ODocument doc = objectDao.objectToDocument(model)
        then: 'state preserved'
        doc.field("name") == 'test'

        when: "converting persisted pojo"
        model = objectDao.save(new Model(name: 't'))
        model.name = 'test'
        doc = objectDao.objectToDocument(model)
        then: 'state preserved'
        doc.field("name") == 'test'

        when: "converting document to pojo"
        doc.field("name", "changed")
        model = objectDao.documentToObject(doc)
        then: 'state preserved'
        model.name == 'changed'
    }

    def "Check object id restore after transaction"() {

        when: "saving raw entity"
        Model model = objectDao.save(new Model(name: "check id"))
        model.getId()
        then: "proxy cant be used outside of transaction"
        thrown(ODatabaseException)

    }

    def "Check object id preserved on detach of new object"() {

        when: "detaching not saved object"
        Model model = context.doInTransaction({ db ->
            Model res = db.save(new Model(name: "test"))
            db.detach(res, true)
        } as SpecificTxAction)
        then: "id is not valid (unsaved)"
        new ORecordId(model.getId()).isNew()

        when: "detaching with mixin"
        model = context.doInTransaction({ db ->
            Model res = objectDao.save(new Model(name: "test"))
            objectDao.detach(res)
        } as SpecificTxAction)
        then: "id is valid (tracked on commit and set correct)"
        !new ORecordId(model.getId()).isNew()
    }

    def "Check duplicate remove"() {

        when: "creating and removing pojo"
        String id = objectDao.detach(objectDao.save(new Model(name: 'test'))).id
        objectDao.delete(id)
        objectDao.delete(id)
        then: "second delete successful"
        true

    }
}
