package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index

import com.orientechnologies.orient.core.index.OIndex
import com.orientechnologies.orient.core.index.OIndexRemote
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
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
        !clazz.getClassIndex("test").getDefinition().isNullValuesIgnored()

        when: "call for already registered indexes"
        def id = idxId(clazz.getClassIndex("test"))
        schemeInitializer.clearModelCache()
        schemeInitializer.register(CompositeIndexModel)
        clazz = db.getMetadata().getSchema().getClass(CompositeIndexModel)
        then: "nothing changed"
        clazz.getClassIndexes().size() == 1
        idxId(clazz.getClassIndex("test")) == id
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("test").getDefinition().getFields() == ["foo", "bar"]
        !clazz.getClassIndex("test").getDefinition().isNullValuesIgnored()
    }

    def "Check index re-create"() {

        when: "index already exist with different type"
        def clazz = db.getMetadata().getSchema().createClass(CompositeIndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex('test', OClass.INDEX_TYPE.DICTIONARY, "foo", "bar")
        def id = idxId(clazz.getClassIndex("test"))
        schemeInitializer.register(CompositeIndexModel)
        clazz = db.getMetadata().getSchema().getClass(CompositeIndexModel)
        then: "index re-created"
        clazz.getClassIndexes().size() == 1
        // skip check for remote test
        id == null || idxId(clazz.getClassIndex("test")) != id
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("test").getDefinition().getFields() == ["foo", "bar"]
        !clazz.getClassIndex("test").getDefinition().isNullValuesIgnored()
    }

    def "Check existing index with different fields"() {

        when: "index already exist with different fields"
        def clazz = db.getMetadata().getSchema().createClass(CompositeIndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex('test', OClass.INDEX_TYPE.DICTIONARY, "foo")
        schemeInitializer.register(CompositeIndexModel)
        then: "error"
        thrown(SchemeInitializationException)

    }

    def "Check existing index with the same fields but different order"() {

        when: "index already exist with different fields order"
        def clazz = db.getMetadata().getSchema().createClass(CompositeIndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex('test', OClass.INDEX_TYPE.NOTUNIQUE, "bar", "foo").getDefinition().setNullValuesIgnored(false)
        def id = idxId(clazz.getClassIndex("test"))
        schemeInitializer.register(CompositeIndexModel)
        clazz = db.getMetadata().getSchema().getClass(CompositeIndexModel)
        then: "index not changed"
        clazz.getClassIndexes().size() == 1
        idxId(clazz.getClassIndex("test")) == id
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

    private Object idxId(OIndex index) {
        return index instanceof OIndexRemote ? null : index.indexId
    }
}