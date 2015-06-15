package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene

import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import org.apache.lucene.analysis.en.EnglishAnalyzer
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 16.06.2015
 */
class LuceneIndexTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene"
    }

    def "Check index creation"() {

        when: "first class registration"
        schemeInitializer.register(LuceneIndexModel)
        def clazz = db.getMetadata().getSchema().getClass(LuceneIndexModel)
        then: "indexes created"
        clazz.getClassIndexes().size() == 2
        clazz.getClassIndex("LuceneIndexModel.defaults").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        clazz.getClassIndex("LuceneIndexModel.custom").getType() == OClass.INDEX_TYPE.FULLTEXT.name()

        when: "call for already registered indexes"
        schemeInitializer.clearModelCache()
        schemeInitializer.register(LuceneIndexModel)
        clazz = db.getMetadata().getSchema().getClass(LuceneIndexModel)
        then: "nothing changed"
        clazz.getClassIndexes().size() == 2
        clazz.getClassIndex("LuceneIndexModel.defaults").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        clazz.getClassIndex("LuceneIndexModel.custom").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
    }

    def "Check index re-create"() {

        when: "index already exist with different analyzer"
        def clazz = db.getMetadata().getSchema().createClass(LuceneIndexModel)
        clazz.createProperty("defaults", OType.STRING)
        clazz.createProperty("custom", OType.STRING)
        clazz.createIndex("LuceneIndexModel.defaults", "FULLTEXT", null, null, "LUCENE", ["defaults"] as String[]);
        clazz.createIndex("LuceneIndexModel.custom", "FULLTEXT", null, null, "LUCENE", ["custom"] as String[]);
        schemeInitializer.register(LuceneIndexModel)
        then: "indexes re-created"
        clazz.getClassIndexes().size() == 2
        clazz.getClassIndex("LuceneIndexModel.defaults").getMetadata() == null
        clazz.getClassIndex("LuceneIndexModel.custom").getMetadata().field(LuceneIndexFieldExtension.ANALYZER) == EnglishAnalyzer.name
    }

    def "Check existing index with different fields"() {

        when: "index already exist with different fields"
        def clazz = db.getMetadata().getSchema().createClass(LuceneIndexModel)
        clazz.createProperty("defaults", OType.STRING)
        clazz.createProperty("custom", OType.STRING)
        clazz.createIndex('LuceneIndexModel.defaults', OClass.INDEX_TYPE.FULLTEXT, "custom")
        schemeInitializer.register(LuceneIndexModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    def "Check existing index with incompatible type"() {

        when: "index already exist with different type"
        def clazz = db.getMetadata().getSchema().createClass(LuceneIndexModel)
        clazz.createProperty("defaults", OType.STRING)
        clazz.createIndex('LuceneIndexModel.defaults', OClass.INDEX_TYPE.DICTIONARY, "defaults")
        schemeInitializer.register(LuceneIndexModel)
        then: "error"
        thrown(SchemeInitializationException)
    }
}