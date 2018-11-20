package ru.vyarus.guice.persist.orient.transaction

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import ru.vyarus.guice.persist.orient.transaction.support.ComplexModificationService
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
@UseModules(PackageSchemeModule)
class CompositeTransaction extends AbstractTest {

    @Inject
    ComplexModificationService service

    @Override
    void setup() {
        context.doWithoutTransaction({ db ->
            10.times { db.save(new VertexModel(name: 'name' + it)) }
        } as SpecificTxAction<Void, ODatabaseObject>)
    }

    def "Check using graph api to access object data"() {
        when: "retrieve rows created with object api (in data initializer) using graph api"
        List res = service.selectWithGraph()
        then:
        res.size() == 10
    }

    def "Check transaction isolation in pool"() {
        when: "Inserting element in object transaction and select everything using graph"
        List res = context.doInTransaction({ db ->
            db.save(new VertexModel(name: 'name11'))
            service.selectWithGraph()
        } as SpecificTxAction<List, ODatabaseObject>)
        then: "Graph connection select just inserted element"
        res.size() == 11
    }
}
