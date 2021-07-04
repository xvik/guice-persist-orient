package ru.vyarus.guice.persist.orient.study.index.rebuild

import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.study.index.ci.IndexCiRepository
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import ru.vyarus.guice.persist.orient.util.transactional.TransactionalTest
import spock.guice.UseModules

import javax.inject.Inject

/**
 * https://github.com/xvik/guice-persist-orient/issues/16 reproduction test.
 * I was not able to reproduce, but changed index creation to officially supported way:
 * https://orientdb.org/docs/3.1.x/indexing/Indexes.html#indexes-and-null-values
 *
 * @author Vyacheslav Rusakov
 * @since 10.01.2018
 */
@UseModules(RepositoryTestModule)
class IndexRebuildProblemTest extends AbstractTest {

    @Inject
    IndexCiRepository repository
    @Inject
    ObjectSchemeInitializer initializer

    @Override
    void setup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            initializer.register(RebuildIndexCaseModel)
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
    }

    @Override
    void cleanup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            db.getEntityManager().deregisterEntityClass(RebuildIndexCaseModel)
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
    }

    @TransactionalTest(OTransaction.TXTYPE.NOTX)
    def "Check index always dropped case"() {

        when: "put value into index to track drop"
        def db = context.getConnection()
        def index = db.metadata.indexManager.getIndex('RebuildIndexCaseModel.foo')
        assert index.size == 0
        // need instance to put into index
        db.save(new RebuildIndexCaseModel(foo: 'sample'))

        then: "index not empty"
        index.size > 0

        when: "init entity one more time to reproduce drop on db start"
        initializer.clearModelCache()
        initializer.register(RebuildIndexCaseModel)
        db.getMetadata().getSchema().synchronizeSchema()

        then: "index not dropped"
        db.getMetadata().reload()
        db.metadata.indexManager.getIndex('RebuildIndexCaseModel.foo').size > 0

    }
}
