package ru.vyarus.guice.persist.orient.repository.command.live.advanced

import com.orientechnologies.common.exception.OException
import com.orientechnologies.orient.core.db.record.ORecordOperation
import com.orientechnologies.orient.core.sql.query.OLiveResultListener
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.RecordOperation
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 10.10.2017
 */
@UseModules(RepositoryTestModule)
class AdvancedLiveExecutionTest extends AbstractTest {

    @Inject
    AdvancedLiveCases repository
    @Inject
    Listener listener
    @Inject
    VertexListener vertexListener

    def "Check advanced cases"() {

        setup: "subscribe listener"
        int token = repository.subscribe(listener)

        when: "insert value"
        Model saved = context.doInTransaction({ db ->
            def res = repository.save(new Model(name: "justnow"))
            db.detach(res, true)
        } as SpecificTxAction)
        sleep(70)
        then: "listener called"
        listener.lastToken == token
        listener.lastOp == RecordOperation.CREATED
        listener.last instanceof Model
        listener.last.name == saved.name
    }

    def "Check transaction wrapper disable"() {

        setup:
        listener.reset()
    }

    def "Check selective query"() {

        setup: "subscribe listener"
        listener.reset()
        int token = repository.subscribeConditional(listener)

        when: "insert value"
        context.doInTransaction({ db ->
            def res = repository.save(new Model(name: "justnow"))
            db.detach(res, true)
        } as SpecificTxAction)
        sleep(70)
        then: "listener not called"
        listener.last == null

        when: "insert matched value"
        Model saved = context.doInTransaction({ db ->
            def res = repository.save(new Model(name: "justnow", cnt: 2))
            db.detach(res, true)
        } as SpecificTxAction)
        sleep(70)
        then: "listener called"
        listener.last != null
        listener.last.name == saved.name
    }

    def "Check vertex mapping"() {

        setup: "subscribe listener"
        int token = repository.subscribeVertex(vertexListener)

        when: "insert value"
        VertexModel saved = context.doInTransaction({ db ->
            def res = db.save(new VertexModel(name: "justnow"))
            db.detach(res, true)
        } as SpecificTxAction)
        sleep(70)
        then: "listener called"
        vertexListener.lastToken == token
        vertexListener.lastOp == RecordOperation.CREATED
        vertexListener.last instanceof Vertex
        vertexListener.last.getProperty("name") == saved.name
    }

    def "Check simple listener wrapping"() {

        when: "subscribe with the simple listener"
        def res
        repository.subscribeDoc(new OLiveResultListener() {
            @Override
            void onLiveResult(int iLiveToken, ORecordOperation iOp) throws OException {
                res = iOp.getRecord()
            }

            @Override
            void onError(int iLiveToken) {
            }

            @Override
            void onUnsubscribe(int iLiveToken) {
            }
        })
        Model saved = context.doInTransaction({ db ->
            def saved = repository.save(new Model(name: "justnow", cnt: 2))
            db.detach(saved, true)
        } as SpecificTxAction)
        sleep(70)
        then: "listener called"
        res != null
        res.field('name') == saved.name
    }

    static class Listener extends AbstractListener<Model> {

        @Inject
        PersistentContext<OObjectDatabaseTx> context;

        @Override
        protected Model postProcess(Model result) {
            return context.connection.detach(result, true)
        }
    }

    static class VertexListener extends AbstractListener<OrientVertex> {

        @Inject
        PersistentContext<OrientBaseGraph> context;

        @Override
        protected OrientVertex postProcess(OrientVertex result) {
            return context.connection.detach(result) as OrientVertex
        }
    }
}
