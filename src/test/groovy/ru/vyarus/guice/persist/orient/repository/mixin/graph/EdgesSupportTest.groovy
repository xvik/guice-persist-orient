package ru.vyarus.guice.persist.orient.repository.mixin.graph

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.impls.orient.OrientEdge
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.mixin.graph.support.EdgesDao
import ru.vyarus.guice.persist.orient.support.model.EdgeModel
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import ru.vyarus.guice.persist.orient.util.transactional.TransactionalTest
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 23.06.2015
 */
@UseModules(RepositoryTestModule)
class EdgesSupportTest extends AbstractTest {

    @Inject
    EdgesDao dao

    @TransactionalTest
    def "Check edges mixin"() {

        setup:
        VertexModel test = dao.save(new VertexModel(name: 'test1'))
        VertexModel test2 = dao.save(new VertexModel(name: 'test2'))

        when: "creating edge"
        EdgeModel edge = dao.createEdge(EdgeModel.class, test, test2);
        then: "edge created"
        edge != null

        when: "updating edge property"
        edge.name = 'edge test'
        dao.updateEdge(edge)
        then: "ok"
        true

        when: "searching for edge"
        edge = dao.findEdge(EdgeModel.class, test, test2)
        then: "found"
        edge != null
        edge.name == 'edge test'

        then: "find counts direction"
        dao.findEdge(EdgeModel, test2, test) == null

        then: "find between correct"
        dao.findEdgeBetween(EdgeModel, test2, test) != null

        when: 'removing edge'
        dao.deleteEdge(edge)
        then: 'removed'
        dao.findEdge(EdgeModel.class, test, test2) == null

        when: 'remove edge between nodes'
        dao.createEdge(EdgeModel.class, test, test2)
        int deleted = dao.deleteEdge(EdgeModel.class, test, test2)
        then: 'removed'
        deleted == 1
        dao.findEdge(EdgeModel.class, test, test2) == null

        when: 'creating edge with object'
        edge = dao.createEdge(test, test2, new EdgeModel(name: 'edge test'))
        then: 'ok'
        edge.name == 'edge test'

        when: "removing as orient edge"
        dao.deleteEdge(dao.objectToEdge(edge))
        then: 'ok'
        true

        when: 'creating edge with document'
        edge = dao.createEdge(EdgeModel.class, test, test2, new ODocument('name', 'edge test'))
        then: 'ok'
        edge.name == 'edge test'
    }

    @TransactionalTest
    def "Check edge conversions"() {

        when: "converting not persisted edge pojo"
        EdgeModel model = new EdgeModel(name: 'test')
        OrientEdge edge = dao.objectToEdge(model)
        then: 'state preserved'
        edge.getProperty("name") == 'test'

        when: "converting persisted pojo"
        VertexModel test = dao.save(new VertexModel(name: 'test1'))
        VertexModel test2 = dao.save(new VertexModel(name: 'test2'))
        model = dao.createEdge(EdgeModel, test, test2)
        model.name = 'test'
        edge = dao.objectToEdge(model)
        then: 'state preserved'
        edge.getProperty("name") == 'test'

        when: "converting edge to pojo"
        edge.setProperty("name", "changed")
        model = dao.edgeToObject(edge)
        then: 'state preserved'
        model.name == 'changed'
    }

    def "Check object id restore after transaction"() {

        setup:
        VertexModel test = dao.save(new VertexModel(name: 'test1'))
        VertexModel test2 = dao.save(new VertexModel(name: 'test2'))

        when: "saving raw entity"
        EdgeModel model = dao.createEdge(test, test2, new EdgeModel(name: "check id"))
        // edge instance can't be detached because orient tries to operate on in/out fields
        String id = context.doInTransaction({db ->
            model.id
        } as SpecificTxAction)
        then: "id correct"
        dao.getEdge(id) != null

    }

    def "Check pure object id restore after transaction"() {

        setup:
        VertexModel test = dao.save(new VertexModel(name: 'test1'))
        VertexModel test2 = dao.save(new VertexModel(name: 'test2'))

        when: "saving raw entity"
        EdgeModel model = dao.createEdge(EdgeModel.class, test, test2)
        // edge instance can't be detached because orient tries to operate on in/out fields
        String id = context.doInTransaction({db ->
            model.id
        } as SpecificTxAction)
        then: "id correct"
        dao.getEdge(id) != null

    }

    def "Check double remove"() {

        setup:
        VertexModel test = dao.save(new VertexModel(name: 'test1'))
        VertexModel test2 = dao.save(new VertexModel(name: 'test2'))

        when: "creating and removing edge pojo"
        EdgeModel model = dao.createEdge(EdgeModel.class, test, test2)
        // edge instance can't be detached because orient tries to operate on in/out fields
        String id = context.doInTransaction({db ->
            model.id
        } as SpecificTxAction)
        dao.deleteEdge(id)
        dao.deleteEdge(id)
        then: "second delete successful"
    }

    @TransactionalTest
    def "Check edge update as orient edge"() {

        setup:
        VertexModel test = dao.save(new VertexModel(name: 'test1'))
        VertexModel test2 = dao.save(new VertexModel(name: 'test2'))

        when: "creating and updating edge"
        OrientEdge edge = dao.objectToEdge(dao.createEdge(EdgeModel.class, test, test2))
        edge.setProperty("name", "tttest")
        dao.updateEdge(edge)
        then: "updated"
        dao.<EdgeModel>getEdge(edge.getIdentity().toString()).name == "tttest"

    }
}