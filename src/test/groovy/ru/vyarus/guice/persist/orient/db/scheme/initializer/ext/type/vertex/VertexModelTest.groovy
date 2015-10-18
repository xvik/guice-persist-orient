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
        schemeInitializer.register(VertexModel)
        then: "vertex created correctly"
        db.getMetadata().getSchema().getClass(VertexModel).getSuperClassesNames() == ["V"]
    }

    def "Check entity already registered"() {
        when: "db already contains entity"
        db.getEntityManager().registerEntityClass(VertexModel)
        schemeInitializer.register(VertexModel)
        then: "type altered"
        db.getMetadata().getSchema().getClass(VertexModel).getSuperClassesNames() == ["V"]
    }

    def "Check entity already registered with correct type"() {
        when: "db already contains entity"
        // first time creating entity
        schemeInitializer.register(VertexModel)
        // here is what we check
        schemeInitializer.register(VertexModel)
        then: "initializer updated graph"
        db.getMetadata().getSchema().getClass(VertexModel).getSuperClassesNames() == ["V"]
    }

    def "Check hierarchical entity registration"() {

        when: "empty db"
        schemeInitializer.register(ComplexVertexModel)
        then: "initializer should create record for base VertexModel extends V"
        db.getMetadata().getSchema().getClass(VertexModel).getSuperClassesNames() == ["V"]
        db.getMetadata().getSchema().getClass(ComplexVertexModel).getSuperClassesNames() == ["VertexModel"]
    }

    def "Check base model already registered without graph support"() {

        when: "register base entity without support and try to map extending entity"
        db.getEntityManager().registerEntityClass(VertexModel)
        schemeInitializer.register(ComplexVertexModel)
        then: "entity superclass changed"
        db.getMetadata().getSchema().getClass(VertexModel).getSuperClassesNames() == ["V"]
        db.getMetadata().getSchema().getClass(ComplexVertexModel).getSuperClassesNames() == ["VertexModel"]
    }
}