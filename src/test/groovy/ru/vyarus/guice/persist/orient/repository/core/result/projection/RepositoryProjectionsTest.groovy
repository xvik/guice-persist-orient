package ru.vyarus.guice.persist.orient.repository.core.result.projection

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 01.11.2014
 */
@UseModules(RepositoryTestModule)
class RepositoryProjectionsTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<OrientGraph> graphTemplate
    @Inject
    RepositoryResultProjections repository

    def "Check projection"() {

        context.doInTransaction({ db ->
            10.times {
                db.save(new Model(name: "name$it", nick: "nick$it"))
            }
        } as SpecificTxAction)

        when: "calling count aggregation"
        int cnt = repository.getCount()
        then: "result unwrapped from ODocument"
        cnt == 10

        when: "calling single field query"
        List<String> res = repository.getNames()
        then: "list of ODocument's returned without flattening"
        res.size() == 10
        res[0] instanceof String

        when: "calling single field query"
        String[] res2 = repository.getNamesArray()
        then: "result list unwrapped from ODocument"
        res2.length == 10
        res2[0].startsWith('name')

        when: "need single element from list query"
        String name = repository.getOneName()
        then: "first element taken and projection will work"
        name.startsWith('name')
    }

    def "Graph connections"() {
        graphTemplate.doInTransaction({ db ->
            10.times {
                db.addVertex("class:$VertexModel.simpleName" as String, "name", "name$it", "nick", "nick$it")
            }
            db.addVertex("class:$SingleValueVertex.simpleName" as String, "name", "sample")
        } as SpecificTxAction)

        when: "call names selection under graph connection"
        String[] res = repository.getGraphNamesArray()
        then: "returned list of vertexes still recognized and flatten"
        res.size() == 10
        res[0].startsWith('name')

        when: "calling count under graph connection"
        int cnt = repository.getGraphCount()
        then: "vertex list unwrapped from ODocument"
        cnt == 10

        when: "calling single field query under graph connection"
        List<String> res2 = repository.getGraphNames()
        then: "list of Vertex's returned without flattening"
        res2.size() == 10
        res2[0] instanceof String

        when: "requesting vertex with single element"
        Vertex res3 = repository.getSingleValueVertex()
        then: "projection not performed"
        res3 instanceof Vertex
    }
}