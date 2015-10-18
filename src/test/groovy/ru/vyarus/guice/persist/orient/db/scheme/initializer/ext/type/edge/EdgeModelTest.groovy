package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge

import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.ComplexVertexModel
import ru.vyarus.guice.persist.orient.support.model.EdgeModel

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
        then: "edge created correctly"
        db.getMetadata().getSchema().getClass(EdgeModel).getSuperClassesNames() == ["E"]
    }

    def "Check entity already registered"() {
        when: "db already contains entity"
        db.getEntityManager().registerEntityClass(EdgeModel)
        schemeInitializer.register(EdgeModel)
        then: "type altered"
        db.getMetadata().getSchema().getClass(EdgeModel).getSuperClassesNames() == ["E"]
    }

    def "Check entity already registered with correct type"() {
        when: "db already contains entity"
        // first time creating entity
        schemeInitializer.register(EdgeModel)
        // here is what we check
        schemeInitializer.register(EdgeModel)
        then: "initializer updated graph"
        db.getMetadata().getSchema().getClass(EdgeModel).getSuperClassesNames() == ["E"]
    }

    def "Check base model already registered without graph support"() {

        when: "register base entity without support and try to map extending entity"
        db.getEntityManager().registerEntityClass(EdgeModel)
        schemeInitializer.register(ComplexEdgeModel)
        then: "entity superclass changed"
        db.getMetadata().getSchema().getClass(EdgeModel).getSuperClassesNames() == ["E"]
        db.getMetadata().getSchema().getClass(ComplexEdgeModel).getSuperClassesNames() == ["EdgeModel"]
    }

    def "Check complex mapping with bad hierarchy"() {
        when: "register complex vertex correctly and then try to register edge in wrong hierarchy"
        schemeInitializer.register(ComplexVertexModel)
        schemeInitializer.register(BadComplexEdgeModel)
        then: "initializer detects vertex type in hierarchy"
        thrown(SchemeInitializationException)
    }
}