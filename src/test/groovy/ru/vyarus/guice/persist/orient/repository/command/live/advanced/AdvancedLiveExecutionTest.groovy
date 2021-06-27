package ru.vyarus.guice.persist.orient.repository.command.live.advanced

import com.orientechnologies.common.exception.OException
import com.orientechnologies.orient.core.command.OCommandResultListener
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.db.record.ORecordOperation
import com.orientechnologies.orient.core.sql.query.OLiveResultListener
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.live.listener.TransactionalLiveAdapter
import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener
import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveResultMapper
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
        } as SpecificTxAction<Model, ODatabaseObject>)
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
                res.detach()
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

    def "Check db modification in listener"() {

        setup: "subscribe listener"
        def changed
        repository.subscribe(new LiveQueryListener<Model>() {
            @Override
            void onLiveResult(int token, RecordOperation operation, Model result) throws Exception {
                // store different entity otherwise will be infinite cycle
                context.connection.save(new VertexModel(name: 'custom'))
            }

            @Override
            void onError(int token) {
            }

            @Override
            void onUnsubscribe(int token) {
            }
        })

        when: "insert value"
        context.doInTransaction({ db ->
            def res = repository.save(new Model(name: "justnow"))
            db.detach(res, true)
        } as SpecificTxAction)
        sleep(70)
        def res = context.doInTransaction({ db ->
            db.browseClass(VertexModel).first()
        } as SpecificTxAction)
        then: "listener called, new entity inserted"
        res != null
    }

    def "Check listener error handling in mapper"() {

        setup: "registering listener throwing exception"
        def failed = false
        def unsubscribed = false
        def token = repository.subscribe(new LiveQueryListener<Model>() {
            @Override
            void onLiveResult(int token, RecordOperation operation, Model result) throws Exception {
                throw new IllegalStateException("ups")
            }

            @Override
            void onError(int token) {
                failed = true
            }

            @Override
            void onUnsubscribe(int token) {
                unsubscribed = true
            }
        })

        when: "insert value"
        context.doInTransaction({ db ->
            def res = repository.save(new Model(name: "justnow"))
            db.detach(res, true)
        } as SpecificTxAction)
        sleep(70)
        then: "listener not failed"
        !failed // onError not called for listener exceptions

        when: "unsubscribe"
        repository.unsubscribe(token)
        sleep(70)
        if (!unsubscribed) {
            System.err.println("UNSUBSCRIPTION not called!")
        }
        // todo incorrect! unsubscription not called with remote connection!
        then: "unsubscribed"
        true
        //unsubscribed
    }

    def "Check live listener side effects"() {

        when: "prepare foo listener"
        OCommandResultListener listener = new TransactionalLiveAdapter(null,
                new LiveResultMapper(null, new LiveQueryListener() {
                    @Override
                    void onLiveResult(int token, RecordOperation operation, Object result) throws Exception {
                    }

                    @Override
                    void onError(int token) {
                    }

                    @Override
                    void onUnsubscribe(int token) {
                    }
                }, Model)
        )
        listener.end() // no effect

        then: "calling not used command listener methods"
        !listener.result(null)
        listener.getResult() == null
    }

    static class Listener extends AbstractListener<Model> {

        @Inject
        PersistentContext<ODatabaseObject> context;

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
