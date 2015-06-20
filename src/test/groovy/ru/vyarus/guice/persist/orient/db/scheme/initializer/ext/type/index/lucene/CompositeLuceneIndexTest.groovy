package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene

import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import org.apache.lucene.analysis.en.EnglishAnalyzer
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene.LuceneIndexFieldExtension

/**
 * @author Vyacheslav Rusakov 
 * @since 20.06.2015
 */
class CompositeLuceneIndexTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene"
    }

    def "Check index creation"() {

        when: "first class registration"
        schemeInitializer.register(CompositeLuceneIndexModel)
        def clazz = db.getMetadata().getSchema().getClass(CompositeLuceneIndexModel)
        then: "indexes created"
        clazz.getClassIndexes().size() == 1
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.FULLTEXT.name()

        when: "call for already registered indexes"
        schemeInitializer.clearModelCache()
        schemeInitializer.register(CompositeLuceneIndexModel)
        clazz = db.getMetadata().getSchema().getClass(CompositeLuceneIndexModel)
        then: "nothing changed"
        clazz.getClassIndexes().size() == 1
        clazz.getClassIndex("test").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
    }

    def "Check index re-create"() {

        when: "index already exist with different analyzer"
        def clazz = db.getMetadata().getSchema().createClass(CompositeLuceneIndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex("test", "FULLTEXT", null, null, "LUCENE", ["foo", "bar"] as String[]);
        schemeInitializer.register(CompositeLuceneIndexModel)
        then: "indexes re-created"
        clazz.getClassIndexes().size() == 1
        clazz.getClassIndex("test").getMetadata().field(LuceneIndexFieldExtension.ANALYZER) == EnglishAnalyzer.name
    }

    def "Check existing index with different fields"() {

        when: "index already exist with different fields"
        def clazz = db.getMetadata().getSchema().createClass(CompositeLuceneIndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex("test", "FULLTEXT", null, null, "LUCENE", ["bar", "foo"] as String[]);
        schemeInitializer.register(CompositeLuceneIndexModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    def "Check existing index with incompatible type"() {

        when: "index already exist with different type"
        def clazz = db.getMetadata().getSchema().createClass(CompositeLuceneIndexModel)
        clazz.createProperty("foo", OType.STRING)
        clazz.createProperty("bar", OType.STRING)
        clazz.createIndex('test', OClass.INDEX_TYPE.DICTIONARY, ["foo", "bar"] as String[])
        schemeInitializer.register(CompositeLuceneIndexModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    def "Check multiple indexes definition"() {

        when: "multiple indexes defined"
        schemeInitializer.register(MultipleLuceneIndexesModel)
        def clazz = db.getMetadata().getSchema().getClass(MultipleLuceneIndexesModel)
        then: "indexes created"
        clazz.getClassIndexes().size() == 2
        clazz.getClassIndex("test1").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        clazz.getClassIndex("test2").getType() == OClass.INDEX_TYPE.FULLTEXT.name()

    }
}