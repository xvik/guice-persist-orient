package ru.vyarus.guice.persist.orient.repository.command.query.advanced

import com.orientechnologies.orient.core.command.OCommandResultListener
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
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
 * @since 16.10.2017
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class AdvancedQueryExecutionTest extends AbstractTest {

    @Inject
    AdvancedQueryCases repository

    def "Check listener usage"() {

        when: "call query with listener"
        def listener = new CollectingListener<Model>() {}
        repository.selectObject(listener)
        then: "done"
        listener.results.size() == 10
        listener.results.first() instanceof Model

        when: "call query with result projection"
        listener = new CollectingListener<String>() {}
        repository.selectName(listener)
        then: "done"
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
        repository.selectObject(listener)
        then: "done"
        listener.results.size() == 10
        listener.results.first() instanceof Model
    }

    def "Check orient listener usage"() {
        when: "call query with listener"
        def res = []
        repository.selectSimple(new OCommandResultListener() {
            @Override
            boolean result(Object iRecord) {
                res << iRecord
                return true
            }

            @Override
            void end() {

            }

            @Override
            Object getResult() {
                return null
            }
        })
        then: "done"
        res.size() == 10
        res.first() instanceof ODocument
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
        then: "error"
        def ex = thrown(MethodExecutionException)
        while(!(ex instanceof ResultConversionException)) {ex = ex.getCause()}
        ex.message == 'Failed to convert ODocument to VersionedEntity'
    }

    def "Not void return type"() {

        when: "call query return type"
        // Model, when declared VersionedEntity
        def listener = new CollectingListener<Model>() {}
        repository.selectNotVoid(listener)
        then: "definition error"
        thrown(MethodDefinitionException)
    }

    // IMPORTANT: if listener would be used directly type will be resolved from parameter
    // always create anonymous class to preserve selected generic
    static class CollectingListener<T> implements QueryListener<T> {

        List<T> results = []

        @Override
        boolean onResult(T result) {
            results << result
            return true
        }

        @Override
        void onEnd() {
        }
    }
}
