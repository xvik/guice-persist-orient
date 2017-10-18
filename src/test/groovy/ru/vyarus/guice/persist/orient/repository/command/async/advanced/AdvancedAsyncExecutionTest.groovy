package ru.vyarus.guice.persist.orient.repository.command.async.advanced

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener
import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConversionException
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

    @Inject
    AdvancedAsyncCases repository

    def "Check custom async query listener"() {

        when: "calling async query"
        def listener = new CollectingListener<Model>() {}
        repository.select(listener)
        sleep(70) // required for remote test variation
        then: "query executed synchronously, async execution possible with remote only"
        listener.results.size() == 10
        listener.results.first() instanceof Model

        when: "calling async query for objects"
        listener = new CollectingListener<ODocument>() {}
        repository.selectDoc(listener)
        sleep(70)
        then: "query executed synchronously, async execution possible with remote only"
        listener.results.size() == 10
        listener.results.first() instanceof ODocument


        when: "calling async query for strings"
        listener = new CollectingListener<String>() {}
        repository.selectProjection(listener)
        sleep(70)
        then: "query executed synchronously, async execution possible with remote only"
        listener.results.size() == 10
        listener.results.first() instanceof String

        when: "call query with vertex conversion"
        context.doInTransaction({ db ->
            db.save(new VertexModel(name: "sample"))
        } as SpecificTxAction)
        listener = new CollectingListener<Vertex>() {}
        repository.selectVertex(listener)
        sleep(70)
        then: "done"
        listener.results.size() == 1
        listener.results.first() instanceof Vertex
    }

    def "Check target type resolution from parameter declaration"() {
        when: "call query with no-generic listener"
        // here listener generic is impossible to resolve directly so parameter declaration will be used instead
        def listener = new CollectingListener<Model>()
        repository.select(listener)
        sleep(70)
        then: "done"
        listener.results.size() == 10
        listener.results.first() instanceof Model
    }

    def "Check polymorphic listener declaration"() {

        when: "call query with listener type greater then declared"
        // Model, when declared VersionedEntity
        def listener = new CollectingListener<Model>() {}
        repository.selectPolymorphic(listener)
        sleep(70)
        then: "done"
        listener.results.size() == 10
        listener.results.first() instanceof Model


        when: "call query with listener type greater then declared"
        // here is impossible to detect correct generic
        listener = new CollectingListener<Model>()
        repository.selectPolymorphic(listener)
        sleep(70)
        then: "error"
        def ex = thrown(MethodExecutionException)
        while(!(ex instanceof ResultConversionException)) {ex = ex.getCause()}
        ex.message == 'Failed to convert ODocument to VersionedEntity'
    }


    // IMPORTANT: if listener would be used directly type will be resolved from parameter
    // always create anonymous class to preserve selected generic
    static class CollectingListener<T> implements AsyncQueryListener<T> {

        List<T> results = []
        String queryThread = Thread.currentThread().name

        @Override
        boolean onResult(T result) {
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
