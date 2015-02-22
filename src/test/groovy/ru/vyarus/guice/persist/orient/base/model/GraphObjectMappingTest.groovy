package ru.vyarus.guice.persist.orient.base.model

import com.google.inject.Inject
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.model.EdgeModel
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.base.model.support.model4mapper.BadComplexEdgeModel
import ru.vyarus.guice.persist.orient.base.model.support.model4mapper.ComplexVertexModel
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import ru.vyarus.guice.persist.orient.base.model.support.ObjectSchemeTestInitializer
import spock.guice.UseModules

/**
 * Checks various cases of mapping graph models (@EdgeType, @VertexType support)
 *
 * @author Vyacheslav Rusakov 
 * @since 04.08.2014
 */
@UseModules(DefaultModule)
class GraphObjectMappingTest extends AbstractTest {

    @Inject
    ObjectSchemeTestInitializer initializer

    @Override
    void setup() {
        context.doWithoutTransaction({ db ->
            db.getEntityManager().deregisterEntityClasses(Config.MODEL_PKG)
            db.getEntityManager().deregisterEntityClasses("ru.vyarus.guice.persist.orient.base.model.support.model4mapper")
            db.getMetadata().getSchema().synchronizeSchema()
        } as SpecificTxAction<Void, OObjectDatabaseTx>)
    }

    def "Check mapping with empty scheme"() {

        when: "no scheme in db"
        String baseName
        context.doWithoutTransaction({ db ->
            initializer.register(VertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass().getName()
        } as SpecificTxAction)
        then: "vertex created correctly"
        baseName == "V"

        when: "no scheme in db"
        context.doWithoutTransaction({ db ->
            initializer.register(EdgeModel)
            baseName = db.getMetadata().getSchema().getClass(EdgeModel).getSuperClass().getName()
        } as SpecificTxAction)
        then: "edge created correctly"
        baseName == "E"
    }

    def "Check entity already registered"() {
        when: "db already contains entity"
        String baseName
        context.doWithoutTransaction({ db ->
            db.getEntityManager().registerEntityClass(VertexModel)
            initializer.register(VertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer can't update graph"
        thrown(IllegalStateException)
        baseName == null
    }

    def "Check entity already registered with correct type"() {
        when: "db already contains entity"
        String baseName
        context.doWithoutTransaction({ db ->
            // first time creating entity
            initializer.register(VertexModel)
            // here is what we check
            initializer.register(VertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer updated graph"
        baseName == "V"
    }

    def "Check hierarchical entity registration"() {

        when: "empty db"
        String baseName
        context.doWithoutTransaction({ db ->
            initializer.register(ComplexVertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer should create record for base VertexModel extends V"
        baseName == "V"
    }

    def "Check base model already registered without graph support"() {

        when: "register base entity without support and try to map extending entity"
        String baseName
        context.doWithoutTransaction({ db ->
            db.getEntityManager().registerEntityClass(VertexModel)
            initializer.register(ComplexVertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer can't change registered entity type"
        thrown(IllegalStateException)
        baseName == null
    }

    def "Check complex mapping with bad hierarchy"() {
        when: "register complex vertex correctly and then try to register edge in wrong hierarchy"
        String baseName
        context.doWithoutTransaction({ db ->
            initializer.register(ComplexVertexModel)
            initializer.register(BadComplexEdgeModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer can't change registered base entity type"
        thrown(IllegalStateException)
        baseName == null

    }
}