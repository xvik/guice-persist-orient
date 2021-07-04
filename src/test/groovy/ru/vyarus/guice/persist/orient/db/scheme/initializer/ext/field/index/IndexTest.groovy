package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index

import com.orientechnologies.orient.core.index.OIndex
import com.orientechnologies.orient.core.index.OIndexRemote
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class IndexTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index"
    }

    def "Check index creation"() {

        when: "first class registration"
        schemeInitializer.register(IndexModel)
        def clazz = db.getMetadata().getSchema().getClass(IndexModel)
        then: "indexes created"
        clazz.getClassIndexes().size() == 3
        clazz.getClassIndex("IndexModel.foo").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("customName").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        !clazz.getClassIndex("nulls").getDefinition().isNullValuesIgnored()

        when: "call for already registered indexes"
        // mark indexes
        def id1 = id(clazz.getClassIndex("IndexModel.foo"))
        def id2 = id(clazz.getClassIndex("customName"))
        def id3 = id(clazz.getClassIndex("nulls"))
        schemeInitializer.clearModelCache()
        schemeInitializer.register(IndexModel)
        clazz = db.getMetadata().getSchema().getClass(IndexModel)
        then: "nothing changed"
        clazz.getClassIndexes().size() == 3
        id(clazz.getClassIndex("IndexModel.foo")) == id1
        id(clazz.getClassIndex("customName")) == id2
        id(clazz.getClassIndex("nulls")) == id3
        clazz.getClassIndex("IndexModel.foo").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("customName").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        !clazz.getClassIndex("nulls").getDefinition().isNullValuesIgnored()
    }

    def "Check index re-create"() {

        when: "index already exist with different type"
        def clazz = db.getMetadata().getSchema().createClass(IndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createProperty("nulls", OType.STRING)
        clazz.createIndex('IndexModel.foo', OClass.INDEX_TYPE.DICTIONARY, "foo")
        clazz.createIndex('customName', OClass.INDEX_TYPE.DICTIONARY, "bar")
        clazz.createIndex('nulls', OClass.INDEX_TYPE.NOTUNIQUE.name(), null, new ODocument().field("ignoreNullValues", true), ["nulls"] as String[])
        // marking old index
        def id1 = id(clazz.getClassIndex("IndexModel.foo"))
        def id2 = id(clazz.getClassIndex("customName"))
        def id3 = id(clazz.getClassIndex("nulls"))
        schemeInitializer.register(IndexModel)
        clazz = db.getMetadata().getSchema().getClass(IndexModel)
        then: "indexes re-created"
        clazz.getClassIndexes().size() == 3
        // avoid check for remote tests
        id1 == null || id(clazz.getClassIndex("IndexModel.foo")) != id1
        id2 == null ||id(clazz.getClassIndex("customName")) != id2
        id3 == null ||id(clazz.getClassIndex("nulls")) != id3
        clazz.getClassIndex("IndexModel.foo").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("customName").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        !clazz.getClassIndex("nulls").getDefinition().isNullValuesIgnored()
    }

    def "Check existing index with different fields"() {

        when: "index already exist with different fields"
        def clazz = db.getMetadata().getSchema().createClass(IndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex('IndexModel.foo', OClass.INDEX_TYPE.DICTIONARY, "bar")
        schemeInitializer.register(IndexModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    def "Check multiple indexes"() {

        when: "multiple indexes defined"
        schemeInitializer.register(MultipleIndexesModel)
        def clazz = db.getMetadata().getSchema().getClass(MultipleIndexesModel)
        then: "indexes created"
        clazz.getClassIndexes().size() == 2
        clazz.getClassIndex("test1").getType() == OClass.INDEX_TYPE.NOTUNIQUE.name()
        clazz.getClassIndex("test2").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
    }

    private Object id(OIndex index) {
        return index instanceof OIndexRemote ? null : index.indexId
    }
}