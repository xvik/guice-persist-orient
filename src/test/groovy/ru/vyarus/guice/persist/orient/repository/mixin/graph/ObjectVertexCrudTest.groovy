package ru.vyarus.guice.persist.orient.repository.mixin.graph

import com.orientechnologies.orient.core.id.ORecordId
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.mixin.graph.support.ObjectVertexDao
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

    def "Check object id restore after transaction"() {

        when: "saving raw entity"
        VertexModel model = dao.save(new VertexModel(name: "check id"))
        then: "id correct"
        dao.get(model.getId()) != null

    }
}