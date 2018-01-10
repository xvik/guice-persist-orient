package ru.vyarus.guice.persist.orient.study.index.fieldsorder

import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 01.07.2015
 */
@UseModules(RepositoryTestModule)
class IndexFieldsOrderTest extends AbstractTest {

    @Inject
    ObjectSchemeInitializer initializer
    @Inject
    FieldsOrderRepository repository

    def "Check fields order in index"() {

        setup:
        context.doWithoutTransaction({
            def db = context.getConnection()
            initializer.register(FOTest)
            db.getMetadata().getSchema().synchronizeSchema()
            db.getMetadata().reload()
        } as TxAction)
        repository.save(new FOTest(foo: 'foo', bar: 'bar'))

        when: "execute query with fields in the same order"
        ODocument doc = repository.sameOrder()
        then: "index used"
        doc.field("involvedIndexes").contains("test")

        when: "execute query with fields in the reverse order"
        doc = repository.sameOrder()
        then: "index used"
        doc.field("involvedIndexes").contains("test")

        when: "execute query with one field"
        doc = repository.foo()
        then: "composite index NOT USED when searching by single field"
        doc.field("involvedIndexes") == null
    }

    def "Check composite index used for single field"() {

        setup:
        context.doWithoutTransaction({
            def db = context.getConnection()
            initializer.register(FOTest2)
            db.getMetadata().getSchema().synchronizeSchema()
            db.getMetadata().reload()
        } as TxAction)
        repository.save(new FOTest2(foo: 'foo', bar: 'bar'))

        when: "execute query with one field"
        def doc = repository.foo2()
        then: "index USED (why???)"
        doc.field("involvedIndexes").contains("test2")
    }
}