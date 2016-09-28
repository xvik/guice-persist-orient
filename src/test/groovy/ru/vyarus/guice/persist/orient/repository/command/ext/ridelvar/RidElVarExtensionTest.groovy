package ru.vyarus.guice.persist.orient.repository.command.ext.ridelvar

import com.orientechnologies.orient.core.id.ORecordId
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 02.06.2015
 */
@UseModules(RepositoryTestModule)
class RidElVarExtensionTest extends AbstractTest {

    @Inject
    RidElVarCases dao
    @Inject
    Helper helper

    void setup() {
        helper.createPair()
    }

    def "Check parameter binding fail in traverse"() {

        setup:
        VertexModel from = helper.detach(helper.findByName("from"))
        when: "query with parameters"
        List<VertexModel> res = dao.paramPositional(from.id)
        then: "orient can't bind string parameter since 2.2(.10)"
        thrown(MethodExecutionException)
    }

    def "Check rid var bindings"() {
        setup:
        VertexModel from = helper.detach(helper.findByName("from"))

        when: "string rid"
        List<VertexModel> res = dao.string(from.id)
        then: "found"
        res.size() == 1

        when: "orid"
        res = dao.orid(new ORecordId(from.id))
        then: "found"
        res.size() == 1

        when: "object"
        res = dao.object(from)
        then: "found"
        res.size() == 1

        when: "detached object"
        res = dao.object(helper.detach(from))
        then: "found"
        res.size() == 1

        when: "document"
        res = dao.document(helper.getDocument(from.id))
        then: "found"
        res.size() == 1

        when: "vertex"
        res = dao.vertex(helper.getVertex(from.id))
        then: "found"
        res.size() == 1

        when: "universal"
        res = dao.universal(helper.getVertex(from.id))
        then: "found"
        res.size() == 1
    }

    def "Check error cases"() {

        when: "bad string"
        dao.string("dfddfd")
        then: "invalid rid"
        thrown(MethodExecutionException)

        when: "null"
        dao.universal(null)
        then: "null not allowed"
        thrown(MethodExecutionException)

        when: "not saved object"
        dao.universal(new VertexModel(name: 'test'))
        then: "null not allowed"
        thrown(MethodExecutionException)

        when: 'check null'
        res = dao.selectIterable(null)
        then: 'found'
        thrown(MethodExecutionException)
    }

    def "Check list cases"() {

        setup:
        def all = helper.all()

        when: 'select by list of objects'
        List<VertexModel> res = dao.selectList(all)
        then: 'found'
        res.size() == 2

        when: 'select by iterable of objects'
        res = dao.selectIterable(all)
        then: 'found'
        res.size() == 2

        when: 'select by list of objects'
        res = dao.selectIterator(all.iterator())
        then: 'found'
        res.size() == 2

        when: 'select by vararg (array)'
        res = dao.selectVararg(all[0], all[1])
        then: 'found'
        res.size() == 2
    }
}