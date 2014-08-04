package ru.vyarus.guice.persist.orient.base

import com.google.inject.Inject
import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.tx.OTransaction
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.model.EdgeModel
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.model4mapper.BadComplexEdgeModel
import ru.vyarus.guice.persist.orient.support.model4mapper.ComplexVertexModel
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import ru.vyarus.guice.persist.orient.support.scheme.ObjectSchemeTestInitializer
import spock.guice.UseModules
import spock.lang.Specification

/**
 * Checks various cases of mapping graph models (@EdgeType, @VertexType support)
 *
 * @author Vyacheslav Rusakov 
 * @since 04.08.2014
 */
@UseModules(DefaultModule)
class GraphObjectMappingTest extends Specification {

    @javax.inject.Inject
    PersistService persist
    @javax.inject.Inject
    SpecificTxTemplate<OObjectDatabaseTx> template

    @Inject
    ObjectSchemeTestInitializer initializer

    void setup() {
        persist.start()
    }

    void cleanup() {
        // truncate db
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            db.getStorage().clusterInstances.each({ it?.delete() });
            db.getEntityManager().deregisterEntityClasses(Config.MODEL_PKG)
            db.getEntityManager().deregisterEntityClasses("ru.vyarus.guice.persist.orient.support.model4mapper")

            def schema = db.getMetadata().getSchema()
            def dropClass = { Class cls ->
                def name = cls.simpleName
                if (schema.getClass(name)) schema.dropClass(name)
            }
            dropClass(BadComplexEdgeModel)
            dropClass(ComplexVertexModel)
            dropClass(VertexModel)
        } as SpecificTxAction<Void, OObjectDatabaseTx>)
        persist.stop()
    }

    //using different db for test because of more aggressive cleanup - to not interference with other tests
    def static normalUrl;
    static {
        normalUrl = Config.DB
        Config.DB = "memory:schemeTest"
    }

    void cleanupSpec() {
        Config.DB = normalUrl
    }

    def "Check mapping with empty scheme"() {

        when: "no scheme in db"
        String baseName
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            initializer.register(VertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass().getName()
        } as SpecificTxAction)
        then: "vertex created correctly"
        baseName == "V"

        when: "no scheme in db"
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            initializer.register(EdgeModel)
            baseName = db.getMetadata().getSchema().getClass(EdgeModel).getSuperClass().getName()
        } as SpecificTxAction)
        then: "edge created correctly"
        baseName == "E"
    }

    def "Check entity already registered"() {
        when: "db already contains entity"
        String baseName
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
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
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            // first time creating entity
            initializer.register(VertexModel)
            // here is what we check
            initializer.register(VertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer can't update graph"
        baseName == "V"
    }

    def "Check hierarchical entity registration"() {

        when: "empty db"
        String baseName
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            initializer.register(ComplexVertexModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer should create record for base VertexModel extends V"
        baseName == "V"
    }

    def "Check base model already registered without graph support"() {

        when: "register base entity without support and try to map extending entity"
        String baseName
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
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
        template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), { db ->
            initializer.register(ComplexVertexModel)
            initializer.register(BadComplexEdgeModel)
            baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        } as SpecificTxAction)
        then: "initializer can't change registered base entity type"
        thrown(IllegalStateException)
        baseName == null

    }
}