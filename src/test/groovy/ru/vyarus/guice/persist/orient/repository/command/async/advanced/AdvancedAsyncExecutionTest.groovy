package ru.vyarus.guice.persist.orient.repository.command.async.advanced

import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener
import ru.vyarus.guice.persist.orient.support.model.Model
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
        then: "query executed synchronously, async execution possible with remote only"
        listener.results.size() == 10
        listener.results.first() instanceof Model

        when: "calling async query for objects"
        listener = new CollectingListener<ODocument>() {}
        repository.selectDoc(listener)
        then: "query executed synchronously, async execution possible with remote only"
        listener.results.size() == 10
        listener.results.first() instanceof ODocument


        when: "calling async query for strings"
        listener = new CollectingListener<String>() {}
        repository.selectProjection(listener)
        then: "query executed synchronously, async execution possible with remote only"
        listener.results.size() == 10
        listener.results.first() instanceof String
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
