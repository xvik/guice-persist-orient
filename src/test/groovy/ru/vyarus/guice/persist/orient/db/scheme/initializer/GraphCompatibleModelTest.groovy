package ru.vyarus.guice.persist.orient.db.scheme.initializer

import com.google.inject.Inject
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.EdgeModel
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 04.08.2014
 */
@UseModules(PackageSchemeModule)
class GraphCompatibleModelTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<OrientBaseGraph> graphTemplate;

    def "Check scheme correctly initialized from annotated pojo"() {

        when: "creating vertex from simple class"
        graphTemplate.doInTransaction({ db ->
            db.addVertex("class:$Model.simpleName" as String)
        } as SpecificTxAction)
        then: "error because of incompatible class"
        thrown(IllegalArgumentException)

        when: "creating vertex from annotated class"
        graphTemplate.doInTransaction({ db ->
            db.addVertex("class:$VertexModel.simpleName" as String)
        } as SpecificTxAction)
        then: "success"
        true

        when: "creating edge from annotated class"
        graphTemplate.doInTransaction({ db ->
            Vertex vin = db.addVertex("class:$VertexModel.simpleName" as String);
            Vertex vout = db.addVertex("class:$VertexModel.simpleName" as String);
            db.addEdge("class:$EdgeModel.simpleName" as String, vin, vout, 'tst')
        } as SpecificTxAction)
        then: "success"
        true
    }

    def "Check graph entity usage from other connections"() {

        when: "creating vertex record from object connection"
        context.doInTransaction({ db ->
            db.save(new VertexModel(name: 'tst', nick: 'tst'));
        } as SpecificTxAction)
        VertexModel model = context.doInTransaction({ db ->
            db.detach(db.browseClass(VertexModel.class).first(), true);
        } as SpecificTxAction<VertexModel, OObjectDatabaseTx>)
        then: "created and visible"
        model != null
        model.name == 'tst'
    }
}