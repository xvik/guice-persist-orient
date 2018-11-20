package ru.vyarus.guice.persist.orient.study

import com.google.common.collect.Lists
import com.google.inject.Inject
import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(PackageSchemeModule)
class CreationInterchangeTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<ODatabaseDocument> documentTemplate;
    @Inject
    SpecificTxTemplate<OrientBaseGraph> graphTemplate;

    def "Check object visible"() {
        when: "creating object"
        context.doInTransaction({ db ->
            db.save(new VertexModel(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)
        List objects = context.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select from VertexModel"))
        } as SpecificTxAction<List, ODatabaseObject>)
        List documents = documentTemplate.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select from VertexModel"))
        } as SpecificTxAction<List, ODatabaseDocument>)
        List graphs = graphTemplate.doInTransaction({ db ->
            // graph returns iterable
            Lists.newArrayList(db.getVerticesOfClass(VertexModel.simpleName))
        } as SpecificTxAction<List, OrientBaseGraph>)
        List graphsQuery = graphTemplate.doInTransaction({ db ->
            Lists.newArrayList(db.command(new OSQLSynchQuery<Object>("select from VertexModel")))
        } as SpecificTxAction<List, OrientBaseGraph>)
        then: "other connections see it"
        objects.size() == 1
        documents.size() == 1
        graphs.size() == 1
        graphsQuery.size() == 1
    }

    def "Check document visible"() {
        when: "creating document"
        documentTemplate.doInTransaction({ db ->
            ODocument doc = new ODocument(VertexModel.simpleName)
            doc.field('name', 'John')
            doc.field('nick', 'Doe')
            db.save(doc)
        } as SpecificTxAction)
        List objects = context.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select from VertexModel"))
        } as SpecificTxAction<List, ODatabaseObject>)
        List documents = documentTemplate.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select from VertexModel"))
        } as SpecificTxAction<List, ODatabaseDocument>)
        List graphs = graphTemplate.doInTransaction({ db ->
            // graph returns iterable
            Lists.newArrayList(db.getVerticesOfClass(VertexModel.simpleName))
        } as SpecificTxAction<List, OrientBaseGraph>)
        List graphsQuery = graphTemplate.doInTransaction({ db ->
            Lists.newArrayList(db.command(new OSQLSynchQuery<Object>("select from VertexModel")))
        } as SpecificTxAction<List, OrientBaseGraph>)
        then: "other connections see it"
        objects.size() == 1
        documents.size() == 1
        graphs.size() == 1
        graphsQuery.size() == 1
    }

    def "Check vertex visible"() {
        when: "creating document"
        graphTemplate.doInTransaction({ db ->
            db.addVertex("class:$VertexModel.simpleName" as String, "name", "John", "nick", "Doe")
        } as SpecificTxAction)
        List objects = context.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select from VertexModel"))
        } as SpecificTxAction<List, ODatabaseObject>)
        List documents = documentTemplate.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select from VertexModel"))
        } as SpecificTxAction<List, ODatabaseDocument>)
        List graphs = graphTemplate.doInTransaction({ db ->
            // graph returns iterable
            Lists.newArrayList(db.getVerticesOfClass(VertexModel.simpleName))
        } as SpecificTxAction<List, OrientBaseGraph>)
        List graphsQuery = graphTemplate.doInTransaction({ db ->
            Lists.newArrayList(db.command(new OSQLSynchQuery<Object>("select from VertexModel")))
        } as SpecificTxAction<List, OrientBaseGraph>)
        then: "other connections see it"
        objects.size() == 1
        documents.size() == 1
        graphs.size() == 1
        graphsQuery.size() == 1
    }
}