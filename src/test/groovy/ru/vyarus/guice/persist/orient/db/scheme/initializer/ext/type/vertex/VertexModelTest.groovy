package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex

import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest
import ru.vyarus.guice.persist.orient.support.model.VertexModel

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
class VertexModelTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex"
    }

    def "Check mapping with empty scheme"() {

        when: "no scheme in db"
        String baseName
        schemeInitializer.register(VertexModel)
        baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass().getName()
        then: "vertex created correctly"
        baseName == "V"
    }

    def "Check entity already registered"() {
        when: "db already contains entity"
        String baseName
        db.getEntityManager().registerEntityClass(VertexModel)
        schemeInitializer.register(VertexModel)
        baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        then: "type altered"
        baseName == "V"
    }

    def "Check entity already registered with correct type"() {
        when: "db already contains entity"
        String baseName
        // first time creating entity
        schemeInitializer.register(VertexModel)
        // here is what we check
        schemeInitializer.register(VertexModel)
        baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        then: "initializer updated graph"
        baseName == "V"
    }

    def "Check hierarchical entity registration"() {

        when: "empty db"
        String baseName
        schemeInitializer.register(ComplexVertexModel)
        baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        then: "initializer should create record for base VertexModel extends V"
        baseName == "V"
    }

    def "Check base model already registered without graph support"() {

        when: "register base entity without support and try to map extending entity"
        String baseName
        db.getEntityManager().registerEntityClass(VertexModel)
        schemeInitializer.register(ComplexVertexModel)
        baseName = db.getMetadata().getSchema().getClass(VertexModel).getSuperClass()?.getName()
        then: "entity superclass changed"
        baseName == "V"
    }
}