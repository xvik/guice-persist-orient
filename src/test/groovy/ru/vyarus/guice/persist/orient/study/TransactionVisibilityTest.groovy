package ru.vyarus.guice.persist.orient.study

import com.google.inject.Inject
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.tx.OTransaction
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.repository.mixin.crud.support.DocumentDao
import ru.vyarus.guice.persist.orient.repository.mixin.crud.support.ObjectDao
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Provider

/**
 * Shows that changes during transaction are visible between all transaction types
 * @author Vyacheslav Rusakov 
 * @since 25.01.2015
 */
@UseModules(RepositoryTestModule)
class TransactionVisibilityTest extends AbstractTest {

    @Inject
    DocumentDao documentDao
    @Inject
    ObjectDao objectDao
    @Inject
    Provider<OrientGraph> graph;
    @Inject
    SpecificTxTemplate<OrientBaseGraph> graphTemplate;

    def "Check document driven visibility"() {
        setup:
        context.transactionManager.begin()

        when: "creating document and reading it in other types within single transaction"
        ODocument doc = documentDao.create()
        doc.field("name", "test-name")
        documentDao.save(doc)

        def id = doc.field('@rid') as String
        OrientGraph gdb = graph.get();
        Vertex vertex = gdb.getVertex(new ORecordId(id))
        Model model = objectDao.get(id)

        then: "document change visible"
        vertex && (vertex.getProperty("name") == "test-name")
        model && (model.name == "test-name")

        cleanup:
        context.transactionManager.end()
    }

    def "Check object driven visibility"() {
        setup:
        context.transactionManager.begin()

        when: "creating object and reading it in other types within single transaction"
        Model model = objectDao.create()
        model.setName("test-name")
        objectDao.save(model)

        def id = model.getId()
        ODocument doc = documentDao.get(id)
        Vertex vertex = graph.get().getVertex(new ORecordId(id))

        then: "object change visible"
        doc && (doc.field("name") == "test-name")
        vertex && (vertex.getProperty("name") == "test-name")

        cleanup:
        context.transactionManager.end()
    }

    def "Check vertex driven visibility"() {
        setup:
        graphTemplate.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            db.getRawGraph().getMetadata().getSchema().dropClass(Model.simpleName)
            db.createVertexType(Model.simpleName)
        } as SpecificTxAction)
        context.transactionManager.begin()

        when: "creating vertex and reading it in other types within single transaction"
        Vertex vertex = graph.get().addVertex("class:$Model.simpleName" as String, "name", "test-name")
        def id = vertex.getRecord().field('@rid') as String
        ODocument doc = documentDao.get(id)
        Model model = objectDao.get(id)

        then: "vertex change visible"
        doc && (doc.field("name") == "test-name")
        model && (model.name == "test-name")

        cleanup:
        context.transactionManager.end()
    }
}