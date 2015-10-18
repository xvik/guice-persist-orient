package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util

import com.orientechnologies.orient.core.tx.OTransaction
import org.slf4j.LoggerFactory
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
@UseModules(DefaultModule)
class SchemeUtilsDbTest extends AbstractTest {

    def "Check command"() {

        when: "calling command"
        context.doWithoutTransaction({ db ->
            SchemeUtils.command(db, "create class %s extends %s", "Test", "V")
            assert db.getMetadata().getSchema().getClass("Test") != null
        } as SpecificTxAction)
        then: "ok"
        true
    }

    def "Check superclass assignment"() {

        setup:
        context.transactionManager.begin(new TxConfig(OTransaction.TXTYPE.NOTX))
        def db = context.getConnection()
        def logger = LoggerFactory.getLogger(SchemeUtilsDbTest)

        when: "assigning superclass for class without superclass"
        SchemeUtils.command(db, "create class CheckSuper")
        SchemeUtils.assignSuperclass(db, CheckSuper, "V", logger)
        then: "ok"
        db.getMetadata().getSchema().getClass("CheckSuper").getSuperClassesNames() == ["V"]

        when: "assigning one more time"
        SchemeUtils.assignSuperclass(db, CheckSuper, "V", logger)
        then: "nothing changed"
        db.getMetadata().getSchema().getClass("CheckSuper").getSuperClassesNames() == ["V"]

        when: "assigning different class from existing superclass"
        SchemeUtils.assignSuperclass(db, CheckSuper, "E", logger)
        then: "error"
        thrown(IllegalStateException)

        when: "do assignment for not existing class"
        SchemeUtils.assignSuperclass(db, CheckSuper2, "V", logger)
        then: "class created and assigned"
        db.getMetadata().getSchema().getClass("CheckSuper2").getSuperClassesNames() == ["V"]

        cleanup:
        context.transactionManager.end()
    }

    def "Check correct superclass addition"() {

        setup:
        context.transactionManager.begin(new TxConfig(OTransaction.TXTYPE.NOTX))
        def db = context.getConnection()
        def logger = LoggerFactory.getLogger(SchemeUtilsDbTest)
        db.getEntityManager().registerEntityClass(Root.class)

        when: "adding new superclass for class with superclass"
        SchemeUtils.assignSuperclass(db, Root, "V", logger)
        then: "new superclass added"
        db.getMetadata().getSchema().getClass("CheckSuper").getSuperClassesNames().isEmpty()
        db.getMetadata().getSchema().getClass("Root").getSuperClassesNames() == ["CheckSuper", "V"]

        cleanup:
        context.transactionManager.end()
    }

    static class CheckSuper {}

    static class CheckSuper2 {}

    static class Root extends CheckSuper {}
}