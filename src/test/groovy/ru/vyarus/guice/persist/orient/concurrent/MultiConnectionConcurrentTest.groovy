package ru.vyarus.guice.persist.orient.concurrent

import com.google.inject.Inject
import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.core.tx.OTransaction
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(PackageSchemeModule)
class MultiConnectionConcurrentTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<ODatabaseDocument> documentTemplate;
    @Inject
    SpecificTxTemplate<OrientBaseGraph> graphTemplate;
    ExecutorService executor

    @Override
    void setup() {
        executor = Executors.newFixedThreadPool(20)
    }

    @Override
    void cleanup() {
        executor?.shutdown()
    }

    def "Check multi concurrency"() {

        // model correction to be compatible with graph (to be able to create graph vertixes)
        graphTemplate.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            db.getRawGraph().getMetadata().getSchema().dropClass(Model.simpleName)
            db.createVertexType(Model.simpleName)
        } as SpecificTxAction)
        context.doWithoutTransaction({ db ->
            db.getEntityManager().registerEntityClass(Model)
        } as SpecificTxAction)

        when: "for each type 20 thread write and 20 threads read"
        List<Future<?>> executed = []
        int times = 20
        times.times({
            executed << executor.submit({
                documentTemplate.doInTransaction({ db ->
                    ODocument doc = new ODocument(Model.simpleName)
                    doc.field('name', 'John')
                    doc.field('nick', 'Doe')
                    db.save(doc)
                } as SpecificTxAction)
                return null
            })
        })
        times.times({
            executed << executor.submit({
                documentTemplate.doInTransaction({ db ->
                    db.query(new OSQLSynchQuery<Object>("select from Model"))
                } as SpecificTxAction)
                return null
            })
        })

        times.times({
            executed << executor.submit({
                context.doInTransaction({ db ->
                    db.save(new Model(name: 'John', nick: 'Doe'))
                } as SpecificTxAction)
                return null
            })
        })
        times.times({
            executed << executor.submit({
                context.doInTransaction({ db ->
                    db.query(new OSQLSynchQuery<Object>("select from Model"))
                } as SpecificTxAction)
                return null
            })
        })

        times.times({
            executed << executor.submit({
                graphTemplate.doInTransaction({ db ->
                    db.addVertex("class:$Model.simpleName" as String, "name", "John", "nick", "Doe")
                } as SpecificTxAction)
                return null
            })
        })
        times.times({
            executed << executor.submit({
                graphTemplate.doInTransaction({ db ->
                    db.command(new OSQLSynchQuery<Object>("select from Model"))
                } as SpecificTxAction)
                return null
            })
        })

        // lock until finish
        executed.each({ it.get() })
        Long cnt;
        // check data is visible also for all creation types
        List<Model> res;
        context.doInTransaction({ db ->
            cnt = db.countClass(Model)
            res = db.query(new OSQLSynchQuery<Object>("select from Model where name='John'"))
        } as SpecificTxAction)
        then: "db should contain 60 records"
        cnt == times * 3
        res.size() == times * 3
    }
}