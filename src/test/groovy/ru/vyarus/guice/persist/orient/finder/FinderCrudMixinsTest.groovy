package ru.vyarus.guice.persist.orient.finder

import com.google.common.collect.Lists
import com.google.inject.Inject
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.finder.internal.FinderExecutionException
import ru.vyarus.guice.persist.orient.support.finder.DocumentDao
import ru.vyarus.guice.persist.orient.support.finder.ObjectDao
import ru.vyarus.guice.persist.orient.support.finder.mixin.pagination.Page
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

        when: "selecting all records"
        List<Model> all = Lists.newArrayList(objectDao.getAll() as Iterator)
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
    }

    def "Check document crud mixin"() {

        when: "storing document"
        ODocument doc = new ODocument(Model.simpleName)
        doc.field('name', 'name')
        doc.field('nick', 'tst')
        doc = documentDao.save(doc)
        then: "document stored"
        doc.field('@rid')

        when: "selecting all records"
        List<ODocument> all = Lists.newArrayList(documentDao.getAll() as Iterator)
        then: "retrieved"
        all.size() == 1
        all[0] instanceof ODocument

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

    def "Test pagination mixin"() {

        template.doInTransaction({ db ->
            10.times {
                db.save(new Model(name: "name$it", nick: "nick$it"))
            }
        } as SpecificTxAction)

        when: "getting page"
        Page<Model> page = objectDao.getPage(1, 3)
        then:
        page.currentPage == 1
        page.pageSize == 3
        page.totalCount == 10
        page.totalPages == 4
        page.content.size() == 3
        page.content[0].name == 'name0'

        when: "getting page 2"
        page = objectDao.getPage(2, 3)
        then:
        page.currentPage == 2
        page.pageSize == 3
        page.totalCount == 10
        page.totalPages == 4
        page.content.size() == 3
        page.content[0].name == 'name3'

        when: "getting page 2"
        page = objectDao.getPage(4, 3)
        then:
        page.currentPage == 4
        page.content.size() == 1
        page.content[0].name == 'name9'

        when: "calling bad page"
        objectDao.getPage(5, 3)
        then: "bad page exception"
        thrown(FinderExecutionException)

        when: "selecting all records"
        List<Model> all = objectDao.getAll(0, -1);
        then: "all selected"
        all.size() == 10

        when: "selecting non existent range"
        all = objectDao.getAll(20, -1);
        then: "nothing selected"
        all.size() == 0

        when: "getting documents page"
        Page<ODocument> page2 = documentDao.getPage(1, 3)
        then:
        page2.currentPage == 1
        page2.pageSize == 3
        page2.totalCount == 10
        page2.totalPages == 4
        page2.content.size() == 3
        page2.content[0].field('name') == 'name0'
    }
}
