package ru.vyarus.guice.persist.orient.finder

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.finder.FinderResultProjections
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 01.11.2014
 */
@UseModules(AutoScanFinderTestModule)
class FinderProjectionsTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<OrientGraph> graphTemplate
    @Inject
    FinderResultProjections finder

    def "Check projection"() {

        template.doInTransaction({ db ->
            10.times {
                db.save(new Model(name: "name$it", nick: "nick$it"))
            }
        } as SpecificTxAction)

        when: "calling count aggregation"
        int cnt = finder.getCount()
        then: "result unwrapped from ODocument"
        cnt == 10

        when: "calling single field query"
        List<String> res = finder.getNames()
        then: "list of ODocument's returned without flattening"
        res.size() == 10
        res[0] instanceof ODocument

        when: "calling single field query"
        String[] res2 = finder.getNamesArray()
        then: "result list unwrapped from ODocument"
        res2.length == 10
        res2[0] == 'name0'

        when: "need single element from list query"
        String name = finder.getOneName()
        then: "first element taken and projection will work"
        name == 'name0'
    }

    def "Graph connections"() {
        graphTemplate.doInTransaction({ db ->
            10.times {
                db.addVertex("class:$VertexModel.simpleName" as String, "name", "name$it", "nick", "nick$it")
            }
        } as SpecificTxAction)

        when: "call names selection under graph connection"
        String[] res = finder.getGraphNamesArray()
        then: "returned list of vertexes still recognized and flatten"
        res.size() == 10
        res[0] == 'name0'

        when: "calling count under graph connection"
        int cnt = finder.getGraphCount()
        then: "vertex list unwrapped from ODocument"
        cnt == 10

        when: "calling single field query under graph connection"
        List<String> res2 = finder.getGraphNames()
        then: "list of Vertex's returned without flattening"
        res2.size() == 10
        res2[0] instanceof Vertex
    }
}