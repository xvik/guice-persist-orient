package ru.vyarus.guice.persist.orient.study

import com.google.common.collect.Lists
import com.google.inject.Inject
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.core.tx.OTransaction
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(PackageSchemeModule)
class CreationInterchangeTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<ODatabaseDocumentTx> documentTemplate;
    @Inject
    SpecificTxTemplate<OrientBaseGraph> graphTemplate;

    def "Check object visible"() {
        when: "creating object"
        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)
        List objects = template.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select * from Model"))
        } as SpecificTxAction<List, OObjectDatabaseTx>)
        List documents = documentTemplate.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select * from Model"))
        } as SpecificTxAction<List, ODatabaseDocumentTx>)
        List graphs = graphTemplate.doInTransaction({ db ->
            // graph returns iterator
            Lists.newArrayList(db.getVerticesOfClass(Model.simpleName))
        } as SpecificTxAction<List, OrientBaseGraph>)
        List graphsQuery = graphTemplate.doInTransaction({ db ->
            Lists.newArrayList(db.command(new OSQLSynchQuery<Object>("select * from Model")))
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
            ODocument doc = new ODocument(Model.simpleName)
            doc.field('name', 'John')
            doc.field('nick', 'Doe')
            db.save(doc)
        } as SpecificTxAction)
        List objects = template.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select * from Model"))
        } as SpecificTxAction<List, OObjectDatabaseTx>)
        List documents = documentTemplate.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select * from Model"))
        } as SpecificTxAction<List, ODatabaseDocumentTx>)
        List graphs = graphTemplate.doInTransaction({ db ->
            // graph returns iterator
            Lists.newArrayList(db.getVerticesOfClass(Model.simpleName))
        } as SpecificTxAction<List, OrientBaseGraph>)
        List graphsQuery = graphTemplate.doInTransaction({ db ->
            Lists.newArrayList(db.command(new OSQLSynchQuery<Object>("select * from Model")))
        } as SpecificTxAction<List, OrientBaseGraph>)
        then: "other connections see it"
        objects.size() == 1
        documents.size() == 1
        graphs.size() == 1
        graphsQuery.size() == 1
    }

    def "Check vertex visible"() {
        when: "creating document"
        // graph can work with models not extending V, but to create it have to properly register scheme:
        // remove auto registration and create through graph api, after that using graph to update scheme from Class
        graphTemplate.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            db.getRawGraph().getMetadata().getSchema().dropClass(Model.simpleName)
            db.createVertexType(Model.simpleName)
        } as SpecificTxAction)
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            db.getEntityManager().registerEntityClass(Model)
        } as SpecificTxAction)
        graphTemplate.doInTransaction({ db ->
            db.addVertex("class:$Model.simpleName" as String, "name", "John", "nick", "Doe")
        } as SpecificTxAction)
        List objects = template.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select * from Model"))
        } as SpecificTxAction<List, OObjectDatabaseTx>)
        List documents = documentTemplate.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select * from Model"))
        } as SpecificTxAction<List, ODatabaseDocumentTx>)
        List graphs = graphTemplate.doInTransaction({ db ->
            // graph returns iterator
            Lists.newArrayList(db.getVerticesOfClass(Model.simpleName))
        } as SpecificTxAction<List, OrientBaseGraph>)
        List graphsQuery = graphTemplate.doInTransaction({ db ->
            Lists.newArrayList(db.command(new OSQLSynchQuery<Object>("select * from Model")))
        } as SpecificTxAction<List, OrientBaseGraph>)
        then: "other connections see it"
        objects.size() == 1
        documents.size() == 1
        graphs.size() == 1
        graphsQuery.size() == 1
    }
}