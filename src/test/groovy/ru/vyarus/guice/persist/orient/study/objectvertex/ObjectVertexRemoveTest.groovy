package ru.vyarus.guice.persist.orient.study.objectvertex

import com.google.common.collect.Lists
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * Checking assumption that object db remove will work incorrectly on graph
 *
 * @author Vyacheslav Rusakov 
 * @since 13.06.2015
 */
@UseModules(RepositoryTestModule)
class ObjectVertexRemoveTest extends AbstractTest {

    @Inject
    ObjectSchemeInitializer initializer;
    @Inject
    PersistentContext<OrientGraph> graph;

    @Override
    void setup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            initializer.register(ObjectVertex)
            initializer.register(ObjectEdge)
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
        graph.doInTransaction({ db ->
            Vertex from = db.addVertex('class:ObjectVertex')
            Vertex to = db.addVertex('class:ObjectVertex')
            from.addEdge('ObjectEdge', to as Vertex)

            assert Lists.newArrayList(db.getEdgesOfClass('ObjectEdge')).size() == 1
        } as SpecificTxAction)
    }

    @Override
    void cleanup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            db.getEntityManager().deregisterEntityClasses("ru.vyarus.guice.persist.orient.study.objectvertex")
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
    }

    def "Check vertex removal by object api"() {

        when: "removing node with object api"
        context.doInTransaction({ db ->

            ObjectVertex first = db.browseClass(ObjectVertex).iterator().next()
            db.delete(new ORecordId(first.id))

            assert Lists.newArrayList(graph.connection.getEdgesOfClass('ObjectEdge')).size() == 0
        } as SpecificTxAction)
        then: "edge removed from db"
        true
    }

    def "Check correct vertex remove"() {

        when: "removing node with graph api"
        graph.doInTransaction({ db ->

            Vertex first = db.getVerticesOfClass('ObjectVertex').iterator().next()
            first.remove()

            assert Lists.newArrayList(graph.connection.getEdgesOfClass('ObjectEdge')).size() == 0
        } as SpecificTxAction)
        then: "edge remain in db - inconsistent db"
        true
    }

    def "Check vertex update from object api"() {

        when: "updating node with object api"
        context.doInTransaction({ db ->

            ObjectVertex first = db.browseClass(ObjectVertex).iterator().next()
            first.foo = 'ttt'
            db.save(first)

            assert Lists.newArrayList(graph.connection.getEdgesOfClass('ObjectEdge')).size() == 1

            db.query(new OSQLSynchQuery<ObjectVertex>("select from ObjectVertex where out('ObjectEdge').size() > 0")).size() == 1
        } as SpecificTxAction)
        then: "edge remain in db"
        true
    }
}