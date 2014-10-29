package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
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
    }
}
