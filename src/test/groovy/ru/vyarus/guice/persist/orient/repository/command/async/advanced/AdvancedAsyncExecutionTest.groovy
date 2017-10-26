package ru.vyarus.guice.persist.orient.repository.command.async.advanced

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 15.10.2017
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class AdvancedAsyncExecutionTest extends AbstractTest {

    static boolean remote

    @Inject
    AdvancedAsyncCases repository

    void setupSpec() {
        remote = false
    }

    def "Check custom async query listener"() {

        when: "calling async query"
        def listener = new CollectingListener<Model>() {}
        repository.select(listener)
        then: "query executed synchronously"
        listener.results.size() == 10
        listener.results.first() instanceof Model

        when: "calling async query for objects"
        listener = new CollectingListener<ODocument>() {}
        repository.selectDoc(listener)
        then: "query executed synchronously"
        listener.results.size() == 10
        listener.results.first() instanceof ODocument


        when: "calling async query for strings"
        listener = new CollectingListener<String>() {}
        repository.selectProjection(listener)
        then: "query executed synchronously"
        listener.results.size() == 10
        listener.results.first() instanceof String

        when: "call query with vertex conversion"
        context.doInTransaction({ db ->
            db.save(new VertexModel(name: "sample"))
        } as SpecificTxAction)
        listener = new CollectingListener<Vertex>() {}
        repository.selectVertex(listener)
        then: "done"
        listener.results.size() == 1
        listener.results.first() instanceof Vertex
    }

    def "Check target type resolution from parameter declaration"() {
        when: "call query with no-generic listener"
        // here listener generic is impossible to resolve directly so parameter declaration will be used instead
        def listener = new CollectingListener<Model>()
        repository.select(listener)
        then: "done"
        listener.results.size() == 10
        listener.results.first() instanceof Model
    }

    def "Check polymorphic listener declaration"() {

        when: "call query with listener type greater then declared"
        // Model, when declared VersionedEntity
        def listener = new CollectingListener<Model>() {}
        repository.selectPolymorphic(listener)
        then: "done"
        listener.results.size() == 10
        listener.results.first() instanceof Model


        when: "call query with listener type greater then declared"
        // here is impossible to detect correct generic
        listener = new CollectingListener<Model>()
        repository.selectPolymorphic(listener)
        then: "error detected but muted"
        listener.results.isEmpty()
    }


    def "Check non blocking case"() {

        when: "call non blocking query with object conversion"
        def listener = new CollectingListener<Model>()
        def res = repository.selectNonBlock(listener).get()
        then: "done"
        res.size() == 10
        res.first() instanceof Model
    }


    def "Check non blocking with data modification"() {

        when: "call non blocking query and modify db in listener"
        def res = repository.selectNonBlock(new AsyncQueryListener<Model>() {
            @Override
            boolean onResult(Model result) throws Exception {
                result.name = result.name + '_changed'
                // db modification
                context.connection.save(result)
                return true
            }

            @Override
            void onEnd() {
            }
        }).get()
        then: "db modifications are not allowed so listener will fail with an exception, but for local all ok"
        res.size() == (remote ? 0 : 10)
    }

    def "Check non blocking with extra query"() {

        when: "call non blocking query and use extra query in listener"
        def res = repository.selectNonBlock(new AsyncQueryListener<Model>() {
            @Override
            boolean onResult(Model result) throws Exception {
                result.name = result.name + '_changed'
                // extra db query
                context.connection.browseClass(Model.class)
                return true
            }

            @Override
            void onEnd() {
            }
        }).get()
        then: "db operations are not allowed for non blocking async connection, but all ok for local conn"
        res.size() == (remote ? 0 : 10)
    }

    def "Check blocking with data modification"() {

        when: "call blocking query and modify db in listener"
        def res = []
        repository.select(new AsyncQueryListener<Model>() {
            @Override
            boolean onResult(Model result) throws Exception{
                result.name = result.name + '_changed'
                // db modification
                context.connection.save(result)
                res << result
                return true
            }

            @Override
            void onEnd() {
            }
        })
        then: "done"
        res.size() == 10
        res.first() instanceof Model
    }

    // IMPORTANT: if listener would be used directly type will be resolved from parameter
    // always create anonymous class to preserve selected generic
    static class CollectingListener<T> implements AsyncQueryListener<T> {

        List<T> results = []
        String queryThread = Thread.currentThread().name

        @Override
        boolean onResult(T result) throws Exception {
            results << result
            return true
        }

        @Override
        void onEnd() {
            if (Thread.currentThread().name == queryThread) {
                println "NOTE: Listener called at the same thread with query (no async): $queryThread"
            }
        }
    }
}
