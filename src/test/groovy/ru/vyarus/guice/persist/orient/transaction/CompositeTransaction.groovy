package ru.vyarus.guice.persist.orient.transaction

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.modules.BootstrappedModule
import ru.vyarus.guice.persist.orient.support.service.ComplexModificationService
import ru.vyarus.guice.persist.orient.support.service.InsertTransactionalService
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
@UseModules(BootstrappedModule)
class CompositeTransaction extends AbstractTest {

    @Inject
    ComplexModificationService service
    @Inject
    InsertTransactionalService insert

    def "Check using graph api to access object data"() {
        when: "retrieve rows created with object api (in data initializer) using graph api"
        List res = service.selectWithGraph()
        then:
        res.size() == 10
    }

    def "Check transaction isolation in pool"() {
        when: "Inserting element in object transaction and select everything using graph"
        List res = template.doInTransaction({ db ->
            insert.insertRecord()
            service.selectWithGraph()
        } as SpecificTxAction<List, OObjectDatabaseTx>)
        then: "Graph connection select just inserted element"
        res.size() == 11
    }
}
