package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index

import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class CompositeIndexTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index"
    }

    def "Check composite index"() {

        when: "creating model"
        schemeInitializer.register(CompositeIndexModel)
        def clazz = db.getMetadata().getSchema().getClass(CompositeIndexModel)
        then: "index created"
        clazz.getClassIndexes().size() == 1
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("test").getDefinition().getFields() == ["foo", "bar"]

        when: "call for already registered indexes"
        schemeInitializer.clearModelCache()
        schemeInitializer.register(CompositeIndexModel)
        clazz = db.getMetadata().getSchema().getClass(CompositeIndexModel)
        then: "nothing changed"
        clazz.getClassIndexes().size() == 1
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("test").getDefinition().getFields() == ["foo", "bar"]
    }

    def "Check index re-create"() {

        when: "index already exist with different type"
        def clazz = db.getMetadata().getSchema().createClass(CompositeIndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex('test', OClass.INDEX_TYPE.DICTIONARY, "foo", "bar")
        schemeInitializer.register(CompositeIndexModel)
        then: "index re-created"
        clazz.getClassIndexes().size() == 1
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("test").getDefinition().getFields() == ["foo", "bar"]
    }

    def "Check multiple indexes definition"() {

        when: "multiple indexes defined"
        schemeInitializer.register(MultipleIndexesModel)
        def clazz = db.getMetadata().getSchema().getClass(MultipleIndexesModel)
        then: "indexes created"
        clazz.getClassIndexes().size() == 2
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("test2").getType() == OClass.INDEX_TYPE.DICTIONARY.name()

    }
}