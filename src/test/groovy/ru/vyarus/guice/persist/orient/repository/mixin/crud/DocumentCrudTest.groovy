package ru.vyarus.guice.persist.orient.repository.mixin.crud

import com.google.common.collect.Lists
import com.google.inject.Inject
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.mixin.crud.support.DocumentDao
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 13.02.2015
 */
@UseModules(RepositoryTestModule)
class DocumentCrudTest extends AbstractTest {

    @Inject
    DocumentDao documentDao

    def "Check document crud mixin"() {

        setup:
        // use long transaction, because object proxy and document doesn't work outside of transaction scope
        context.transactionManager.begin()

        when: "storing document"
        ODocument doc = documentDao.create()
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

        when: "selecting all records as list"
        all = documentDao.getAllAsList()
        then: "retrieved"
        all.size() == 1
        all[0] instanceof ODocument

        when: "loading document"
        doc = documentDao.get((String) doc.field('@rid'))
        then: "document found"
        doc

        when: "loading document 2"
        doc = documentDao.get(new ORecordId(doc.field('@rid') as String))
        then: "document found"
        doc

        when: "updating document"
        doc.field('name', 'name2')
        doc = documentDao.save(doc)
        doc = documentDao.get((String) doc.field('@rid'))
        then: "updated document stored"
        doc.field('name') == 'name2'

        when: "deleting document"
        documentDao.delete(doc)
        doc = documentDao.get((String) doc.field('@rid'))
        then: "document removed"
        doc == null

        when: "removing document by id"
        doc = documentDao.create()
        doc.field('name', 'name')
        doc.field('nick', 'tst')
        doc = documentDao.save(doc)
        def id = (String) doc.field('@rid')
        documentDao.delete(id)
        doc = documentDao.get(id)
        then: "document removed"
        doc == null

        when: "removing document by orid"
        doc = documentDao.create()
        doc.field('name', 'name')
        doc.field('nick', 'tst')
        doc = documentDao.save(doc)
        id = doc.field('@rid')
        documentDao.delete(new ORecordId(id))
        doc = documentDao.get(id)
        then: "document removed"
        doc == null

        cleanup:
        context.transactionManager.end()
    }

    def "Check document custom mixin"() {

        when: "check generic pass and repository instance"
        Object res = documentDao.doSomething(1, 2)
        then: "called"
        res == null

        when: "check generic pass and connection object"
        res = documentDao.doSomething2(1, 2, Object)
        then: "called"
        res == null

        when: "check incorrect connection type"
        documentDao.badCall()
        then: "bad connection param"
        thrown(RepositoryException)

        when: "check graph connection type, recognized with annotation"
        documentDao.graphCall()
        then: "bad connection param"
        true
    }

    def "Check duplicate remove"() {

        when: "creating and deleting document"
        ODocument doc = documentDao.create()
        doc.field('name', 'name')
        doc = documentDao.save(doc)
        String id = doc.field('@rid')
        documentDao.delete(id)
        documentDao.delete(id)
        then: 'second delete successful'
        true
    }
}