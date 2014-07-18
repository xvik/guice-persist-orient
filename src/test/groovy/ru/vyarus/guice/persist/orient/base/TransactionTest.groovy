package ru.vyarus.guice.persist.orient.base

import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.base.model.Model
import ru.vyarus.guice.persist.orient.base.modules.SimpleModule
import ru.vyarus.guice.persist.orient.base.service.SelectTransactionalService
import ru.vyarus.guice.persist.orient.base.service.InsertTransactionalService
import ru.vyarus.guice.persist.orient.internal.OrientPersistService
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 19.07.2014
 */
@UseModules(SimpleModule.class)
class TransactionTest extends AbstractTest {

    @Inject
    InsertTransactionalService insertService
    @Inject
    SelectTransactionalService selectService

    def "Check transaction declarations"() {
        when: "insert record in one transaction"
        insertService.insertRecord()
        then: "select record in another transaction and check transaction closed"
        selectService.select() != null
        !((OrientPersistService)persist).isTransactionActive()
    }

    def "Check subtransaction"() {
        when: "do inline transactions"
        final Model model = insertService.subtransaction()
        then: "object correctly selected and no errors"
        model !=null
        !((OrientPersistService)persist).isTransactionActive()
    }

    def "Check rollback"() {
        when: "Insert record and fail causing rollback"
        insertService.rollbackCheck()
        then: "Expect exception and no stored object in db"
        thrown(IllegalStateException)
        !((OrientPersistService)persist).isTransactionActive()
        selectService.select() == null
    }

    def "Check nested rollback"() {
        when: "Insert record and fail causing rollback"
        insertService.rollbackSubtransaction()
        then: "Expect exception and no stored object in db"
        thrown(IllegalStateException)
        !((OrientPersistService)persist).isTransactionActive()
        selectService.select() == null
    }
}