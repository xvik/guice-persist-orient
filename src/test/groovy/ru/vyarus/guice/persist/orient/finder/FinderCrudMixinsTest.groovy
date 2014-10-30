package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.finder.internal.FinderExecutionException
import ru.vyarus.guice.persist.orient.support.finder.DocumentDao
import ru.vyarus.guice.persist.orient.support.finder.ObjectDao
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 26.10.2014
 */
@UseModules(AutoScanFinderTestModule)
class FinderCrudMixinsTest extends AbstractTest {

    @Inject
    DocumentDao documentDao
    @Inject
    ObjectDao objectDao

    def "Check object crud mixin"() {

        when: "storing object"
        Model model = new Model(name: 'name', nick: 'tst')
        model = objectDao.save(model)
        then: "object stored"
        model.id

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
    }

    def "Check document crud mixin"() {

        when: "storing document"
        ODocument doc = new ODocument(Model.simpleName)
        doc.field('name', 'name')
        doc.field('nick', 'tst')
        doc = documentDao.save(doc)
        then: "document stored"
        doc.field('@rid')

        when: "loading document"
        doc = documentDao.get((String)doc.field('@rid'))
        then: "document found"
        doc

        when: "loading document 2"
        doc = documentDao.get(new ORecordId(doc.field('@rid') as String))
        then: "document found"
        doc

        when: "updating document"
        doc.field('name', 'name2')
        doc = documentDao.save(doc)
        doc = documentDao.get((String)doc.field('@rid'))
        then: "updated document stored"
        doc.field('name') == 'name2'

        when: "deleting document"
        documentDao.delete(doc)
        doc = documentDao.get((String)doc.field('@rid'))
        then: "document removed"
        doc == null

        when: "removing document by id"
        doc = new ODocument(Model.simpleName)
        doc.field('name', 'name')
        doc.field('nick', 'tst')
        doc = documentDao.save(doc)
        documentDao.delete((String)doc.field('@rid'))
        doc = documentDao.get((String)doc.field('@rid'))
        then: "document removed"
        doc == null

        when: "removing document by orid"
        doc = new ODocument(Model.simpleName)
        doc.field('name', 'name')
        doc.field('nick', 'tst')
        doc = documentDao.save(doc)
        documentDao.delete(new ORecordId((String)doc.field('@rid')))
        doc = documentDao.get((String)doc.field('@rid'))
        then: "document removed"
        doc == null
    }

    def "Check object custom mixin"() {

        when: "check generic pass and finder instance"
        Object res = objectDao.doSomething(1,2)
        then: "called"
        res == null

        when: "check generic pass and connection object"
        res = objectDao.doSomething2(1,2, Object)
        then: "called"
        res == null

        when: "check incorrect connection type"
        objectDao.badCall()
        then: "bad connection param"
        thrown(IllegalArgumentException)

        when: "check graph connection type, recognized with annotation"
        objectDao.graphCall()
        then: "bad connection param"
        true

        when: "invocation fails"
        objectDao.invocationFail()
        then: "fail"
        thrown(FinderExecutionException)

        when: "specific selection"
        objectDao.paramSpecific(1, 1, 'hjh')
        then: "correct specific method chosen"
        true
    }

    def "Check document custom mixin"() {

        when: "check generic pass and finder instance"
        Object res = documentDao.doSomething(1,2)
        then: "called"
        res == null

        when: "check generic pass and connection object"
        res = documentDao.doSomething2(1,2, Object)
        then: "called"
        res == null

        when: "check incorrect connection type"
        documentDao.badCall()
        then: "bad connection param"
        thrown(IllegalArgumentException)

        when: "check graph connection type, recognized with annotation"
        documentDao.graphCall()
        then: "bad connection param"
        true
    }
}
