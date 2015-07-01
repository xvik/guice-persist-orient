package ru.vyarus.guice.persist.orient.study.index.ci

import com.orientechnologies.orient.core.collate.OCaseInsensitiveCollate
import com.orientechnologies.orient.core.collate.ODefaultCollate
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import ru.vyarus.guice.persist.orient.util.transactional.TransactionalTest
import spock.guice.UseModules

import javax.inject.Inject

/**
 * Checking that collate on field and specified on index are independent.
 * Observation: if field is ci - index will be ci in any case;
 * default field become ci if created index is ci; if property collate changed - index will change collate also
 *
 * @author Vyacheslav Rusakov 
 * @since 10.06.2015
 */
@UseModules(RepositoryTestModule)
class IndexCollateTest extends AbstractTest {

    @Inject
    IndexCiRepository repository
    @Inject
    ObjectSchemeInitializer initializer;

    @Override
    void setup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            initializer.register(Test)
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
    }

    @Override
    void cleanup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            db.getEntityManager().deregisterEntityClass(Test)
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
    }

    @TransactionalTest(OTransaction.TXTYPE.NOTX)
    def "Check collate ci appliance"() {

        when: "inserting value"
        def db = context.getConnection()
        db.save(new Test(foo: 'test'))
        then: "property is not ci"
        repository.select('test').size() == 1
        repository.select('teST').size() == 0

        when: "creating ci index"
        repository.createCiIndex()
        db.getMetadata().getSchema().synchronizeSchema()
        db.getMetadata().getSchema().reload()
        then: "property is still non ci according to scheme"
        db.getMetadata().getSchema().getClass('Test').getProperty('foo').getCollate().getName() == ODefaultCollate.NAME

        then: 'index is ci'
        repository.selectByIndex('test').size() == 1
        repository.selectByIndex('teST').size() == 1

        then: "now property become ci (is it normal???)"
        repository.select('test').size() == 1
        repository.select('teST').size() == 1
    }

    @TransactionalTest(OTransaction.TXTYPE.NOTX)
    def "Check ci property index"() {

        setup: "make property ci"
        def db = context.getConnection()
        db.getMetadata().getSchema().getClass('Test').getProperty('foo').setCollate(OCaseInsensitiveCollate.NAME)
        db.getMetadata().getSchema().synchronizeSchema()

        when: "inserting value"
        db.save(new Test(foo: 'test'))
        then: "property is ci"
        repository.select('test').size() == 1
        repository.select('teST').size() == 1

        when: "creating non ci index"
        repository.createNonCiIndex()
        db.getMetadata().getSchema().synchronizeSchema()
        then: "index is not ci"
        db.getMetadata().getIndexManager().getIndex("Test.foo").getDefinition().getCollate().name == OCaseInsensitiveCollate.NAME

        then: 'index is non ci'
        repository.selectByIndex('test').size() == 1
        repository.selectByIndex('teST').size() == 1

        then: "property is ci"
        repository.select('test').size() == 1
        repository.select('teST').size() == 1
    }

    @TransactionalTest(OTransaction.TXTYPE.NOTX)
    def "Check changing property collate after index creation"() {

        setup: "create non ci index on property"
        repository.createNonCiIndex()
        def db = context.getConnection()

        when: "inserting value"
        db.save(new Test(foo: 'test'))
        then: "index is non ci"
        repository.selectByIndex('test').size() == 1
        repository.selectByIndex('teST').size() == 0

        when: "changing property collate to ci"
        db.getMetadata().getSchema().getClass('Test').getProperty('foo').setCollate(OCaseInsensitiveCollate.NAME)
        db.getMetadata().getSchema().synchronizeSchema()
        then: "index become ci too"
        repository.selectByIndex('test').size() == 1
        repository.selectByIndex('teST').size() == 1
    }
}