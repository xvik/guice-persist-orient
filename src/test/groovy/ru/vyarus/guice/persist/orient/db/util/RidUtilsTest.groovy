package ru.vyarus.guice.persist.orient.db.util

import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.object.enhancement.OObjectEntitySerializer
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistException
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.util.support.First
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import ru.vyarus.guice.persist.orient.util.transactional.TransactionalTest
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 04.06.2015
 */
@UseModules([PackageSchemeModule, BootstrapModule])
class RidUtilsTest extends AbstractTest {

    @Inject
    PersistentContext<OrientGraph> graph

    @TransactionalTest
    def "Check valid cases"() {

        setup:
        VertexModel model = context.getConnection().save(new VertexModel(name: 'test'))

        when: "string rid"
        def res = RidUtils.getRid(model.id)
        then: "ok"
        res == model.id

        when: "orid"
        res = RidUtils.getRid(new ORecordId(model.id))
        then: "ok"
        res == model.id

        when: "object proxy"
        res = RidUtils.getRid(model)
        then: "ok"
        res == model.id

        when: "raw object"
        res = RidUtils.getRid(new VertexModel(id: model.id))
        then: "ok"
        res == model.id

        when: "document"
        res = RidUtils.getRid(OObjectEntitySerializer.getDocument(model))
        then: "ok"
        res == model.id

        when: "vertex"
        Vertex vertex = graph.getConnection().getVertex(model.id)
        res = RidUtils.getRid(vertex)
        then: "ok"
        res == model.id

        when: "document without id"
        RidUtils.getRid(new ODocument())
        then: "special case: fake id exist even if object not stored"
        true

        when: "vertex without id"
        RidUtils.getRid(new OrientVertex())
        then: "special case: fake id exist even if object not stored"
        true
    }

    def "Check object id tracking"() {

        when: "detaching just created object inside session"
        Model model = context.doInTransaction({db ->
            Model res = db.save(new Model(name: 'test'))
            db.detach(res, true)
        } as SpecificTxAction)
        then: "invalid id"
        new ORecordId(model.id).isNew()

        when: "detaching just created object inside session with id tracking"
        model = context.doInTransaction({db ->
            Model res = db.save(new Model(name: 'test'))
            Model detached = db.detach(res, true)
            RidUtils.trackIdChange(res, detached)
            detached
        } as SpecificTxAction)
        then: "valid id"
        !new ORecordId(model.id).isNew()

    }

    def "Check error cases"() {

        when: "null"
        RidUtils.getRid(null)
        then:
        thrown(NullPointerException)

        when: "bad string"
        RidUtils.getRid("dsds")
        then:
        thrown(PersistException)

        when: "object without id"
        RidUtils.getRid(new VertexModel())
        then:
        thrown(PersistException)

        when: "object without id field"
        RidUtils.getRid(new First())
        then:
        thrown(PersistException)
    }
}