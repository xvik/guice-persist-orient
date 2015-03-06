package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge

import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.ComplexVertexModel
import ru.vyarus.guice.persist.orient.support.model.EdgeModel
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
class EdgeModelTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge"
    }

    def "Check mapping with empty scheme"() {

        when: "no scheme in db"
        schemeInitializer.register(EdgeModel)
        def baseName = db.getMetadata().getSchema().getClass(EdgeModel).getSuperClass().getName()
        then: "edge created correctly"
        baseName == "E"
    }

    def "Check entity already registered"() {
        when: "db already contains entity"
        String baseName
        db.getEntityManager().registerEntityClass(EdgeModel)
        schemeInitializer.register(EdgeModel)
        baseName = db.getMetadata().getSchema().getClass(EdgeModel).getSuperClass()?.getName()
        then: "type altered"
        baseName == "E"
    }

    def "Check entity already registered with correct type"() {
        when: "db already contains entity"
        String baseName
        // first time creating entity
        schemeInitializer.register(EdgeModel)
        // here is what we check
        schemeInitializer.register(EdgeModel)
        baseName = db.getMetadata().getSchema().getClass(EdgeModel).getSuperClass()?.getName()
        then: "initializer updated graph"
        baseName == "E"
    }

    def "Check base model already registered without graph support"() {

        when: "register base entity without support and try to map extending entity"
        String baseName
        db.getEntityManager().registerEntityClass(EdgeModel)
        schemeInitializer.register(ComplexEdgeModel)
        baseName = db.getMetadata().getSchema().getClass(EdgeModel).getSuperClass()?.getName()
        then: "entity superclass changed"
        baseName == "E"
    }

    def "Check complex mapping with bad hierarchy"() {
        when: "register complex vertex correctly and then try to register edge in wrong hierarchy"
        String baseName
        schemeInitializer.register(ComplexVertexModel)
        schemeInitializer.register(BadComplexEdgeModel)
        baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        then: "initializer can't change registered base entity type"
        thrown(SchemeInitializationException)
        baseName == null

    }
}