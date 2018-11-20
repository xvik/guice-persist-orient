package ru.vyarus.guice.persist.orient.repository.command

import com.google.inject.Inject
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.repository.command.support.DbRecognitionCases
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class DbRecognitionExecutionTest extends AbstractTest {

    @Inject
    DbRecognitionCases dao

    def "Check selects"() {
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object select"
        List<Model> res = dao.selectAll();
        then:
        res.size() == 1

        when: "object select with array conversion"
        Model[] resArr = dao.selectAllAsArray();
        then:
        resArr.length == 1

        when: "object select with single result"
        Model model = dao.selectUnique();
        then:
        model != null
        model.version != null
        model.id != null

        when: "document select"
        List<ODocument> resDoc = dao.selectAllAsDocument();
        then:
        resDoc.size() == 1

        when: "graph select"
        List<Vertex> resVert = dao.selectAllAsVertex();
        then:
        resVert.size() == 1

        when: "graph select"
        List<OrientVertex> resOVert = dao.selectAllAsOrientVertex();
        then:
        resOVert.size() == 1
    }

    def "Select without type"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "document select (no generic)"
        List resGeneric = dao.selectAllNoType();
        then:
        resGeneric.size() == 1

        when: "document select (no generic)"
        List<ODocument> resDoc = dao.selectAllNoType();
        then:
        resDoc.size() == 1
    }

    def "Check update"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        dao.update();
        Model model = dao.selectUnique()
        then:
        model != null
        model.name == 'changed'
    }

    def "Check update with count"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        int cnt = dao.updateWithCount();
        Model model = dao.selectUnique()
        then:
        cnt == 1
        model != null
        model.name == 'changed'
    }

    def "Check update with count as object"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        Integer cnt = dao.updateWithCountObject();
        Model model = dao.selectUnique()
        then:
        cnt == 1
        model != null
        model.name == 'changed'
    }

    def "Check manual connection type definition"() {
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        Model model = context.doInTransaction({ db ->
            // execute both in single transaction to make sure object connection used
            // (if document used for update, select would not see changes)
            dao.updateUsingObjectConnection();
            dao.selectUnique()
        } as SpecificTxAction<Model, ODatabaseObject>)
        then:
        model != null
        model.name == 'changed'
    }

    def "Check iterator cases"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object select for iterable"
        def res = dao.selectAllIterable();
        then: "returned iterable"
        res.iterator().next() != null

        when: "object select for iterator"
        res = dao.selectAllIterator();
        then: "returned iterator"
        res.next() != null

        when: "graph select for iterator"
        res = context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                dao.selectAllVertex().next();
            }
        })
        then: "returned iterator"
        res != null

        when: "graph select for iterable"
        res = context.doInTransaction(new TxAction() {
            @Override
            Object execute() throws Throwable {
                dao.selectAllVertexIterable().iterator().next();
            }
        })
        then: "returned iterable"
        res != null

        when: "object select with set conversion"
        res = dao.selectAllAsSet();
        then: "returned set"
        res instanceof Set
        res.size() == 1

        when: "graph select with set conversion"
        res = dao.selectAllAsSetGraph();
        then: "returned set"
        res instanceof Set
        res.size() == 1

        when: "document connection overridden in select"
        res = dao.documentOverride();
        then: "returned list"
        res.size() == 1
    }
}