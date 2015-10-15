package ru.vyarus.guice.persist.orient.repository.mixin.graph

import com.orientechnologies.orient.core.id.ORecordId
import com.tinkerpop.blueprints.impls.orient.OrientEdge
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.mixin.graph.support.ObjectVertexDao
import ru.vyarus.guice.persist.orient.support.model.EdgeModel
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import ru.vyarus.guice.persist.orient.util.transactional.TransactionalTest
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 22.06.2015
 */
@UseModules(RepositoryTestModule)
class ObjectVertexCrudTest extends AbstractTest {

    @Inject
    ObjectVertexDao dao

    @TransactionalTest
    def "Check object delete"() {

        setup:
        4.times {
            dao.save(new VertexModel(name: 'test' + it))
            if (it > 0) {
                dao.createEdge(dao.findByName('test' + (it - 1)), dao.findByName('test' + it))
            }
        }
        assert dao.countEdges() == 3

        when: 'removing by string'
        dao.delete(dao.findByName('test0').id)
        then: 'ok'
        dao.findByName('test0') == null
        dao.countEdges() == 2

        when: 'removing by orid'
        dao.delete(new ORecordId(dao.findByName('test1').id))
        then: 'ok'
        dao.findByName('test1') == null
        dao.countEdges() == 1

        when: 'removing by object'
        dao.delete(dao.findByName('test2'))
        then: 'ok'
        dao.findByName('test2') == null
        dao.countEdges() == 0
    }

    @TransactionalTest
    def "Check vertex conversions"() {

        when: "converting not persisted vertex pojo"
        VertexModel model = new VertexModel(name: 'test')
        OrientVertex vertex = dao.objectToVertex(model)
        then: 'state preserved'
        vertex.getProperty("name") == 'test'

        when: "converting persisted pojo"
        model = dao.save(new VertexModel(name: 't'))
        model.name = 'test'
        vertex = dao.objectToVertex(model)
        then: 'state preserved'
        vertex.getProperty("name") == 'test'

        when: "converting edge to pojo"
        vertex.setProperty("name", "changed")
        model = dao.vertexToObject(vertex)
        then: 'state preserved'
        model.name == 'changed'
    }

    def "Check object id restore after transaction"() {

        when: "saving raw entity"
        VertexModel model = dao.detach(dao.save(new VertexModel(name: "check id")))
        then: "id correct"
        dao.get(model.getId()) != null

    }

    def "Check duplicate remove"() {

        when: "creating and removing vertex pojo"
        String id = dao.detach(dao.save(new VertexModel(name: "test"))).id
        dao.delete(id)
        dao.delete(id)
        then: "second delete successful"
        true

    }
}