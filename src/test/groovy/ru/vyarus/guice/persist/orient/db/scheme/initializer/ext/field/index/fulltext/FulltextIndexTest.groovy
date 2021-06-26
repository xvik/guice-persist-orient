package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.fulltext

import com.orientechnologies.orient.core.index.OIndex
import com.orientechnologies.orient.core.index.OIndexRemote
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 15.06.2015
 */
class FulltextIndexTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.fulltext"
    }

    def "Check index creation"() {

        when: "first class registration"
        schemeInitializer.register(FulltextIndexModel)
        def clazz = db.getMetadata().getSchema().getClass(FulltextIndexModel)
        then: "indexes created"
        clazz.getClassIndexes().size() == 2

        then: "defaults index correct"
        def defaults = clazz.getClassIndex("FulltextIndexModel.defaults")
        defaults.getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        defaults.getMetadata().field(FulltextIndexFieldExtension.INDEX_RADIX) == true
        defaults.getMetadata().field(FulltextIndexFieldExtension.IGNORE_CHARS) == "'\""
        defaults.getMetadata().field(FulltextIndexFieldExtension.SEPARATOR_CHARS) == " \r\n\t:;,.|+*/\\=!?[]()"
        defaults.getMetadata().field(FulltextIndexFieldExtension.MIN_WORD_LENGTH) == 3
        defaults.getMetadata().field(FulltextIndexFieldExtension.STOP_WORDS) == ["the", "in", "a", "at", "as", "and", "or", "for", "his", "her", "him",
                                                                                 "this", "that", "what", "which", "while", "up", "with", "be", "was", "were", "is"]

        then: "custom index correct"
        def custom = clazz.getClassIndex("all_options")
        custom.getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        custom.getMetadata().field(FulltextIndexFieldExtension.INDEX_RADIX) == false
        custom.getMetadata().field(FulltextIndexFieldExtension.IGNORE_CHARS) == "'"
        custom.getMetadata().field(FulltextIndexFieldExtension.SEPARATOR_CHARS) == "!?"
        custom.getMetadata().field(FulltextIndexFieldExtension.MIN_WORD_LENGTH) == 5
        custom.getMetadata().field(FulltextIndexFieldExtension.STOP_WORDS) == ["of", "the"]


        when: "call for already registered indexes"
        // mark indexes
        def id1 = id(clazz.getClassIndex("FulltextIndexModel.defaults"))
        def id3 = id(clazz.getClassIndex("all_options"))
        schemeInitializer.clearModelCache()
        schemeInitializer.register(FulltextIndexModel)
        clazz = db.getMetadata().getSchema().getClass(FulltextIndexModel)
        then: "nothing changed"
        clazz.getClassIndexes().size() == 2
        id(clazz.getClassIndex("FulltextIndexModel.defaults")) == id1
        id(clazz.getClassIndex("all_options")) == id3
        clazz.getClassIndex("FulltextIndexModel.defaults").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
    }

    def "Check index re-create"() {

        when: "index already exist with different type"
        def clazz = db.getMetadata().getSchema().createClass(FulltextIndexModel)
        clazz.createProperty("defaults", OType.STRING)
        clazz.createProperty("options", OType.STRING)
        clazz.createIndex('FulltextIndexModel.defaults', OClass.INDEX_TYPE.FULLTEXT.name(), null,
                new ODocument().field(FulltextIndexFieldExtension.IGNORE_CHARS, "'"), null, "defaults")
        clazz.createIndex('all_options', OClass.INDEX_TYPE.FULLTEXT, "options")
        // mark indexes
        def id1 = id(clazz.getClassIndex("FulltextIndexModel.defaults"))
        def id3 = id(clazz.getClassIndex("all_options"))
        schemeInitializer.register(FulltextIndexModel)
        clazz = db.getMetadata().getSchema().getClass(FulltextIndexModel)
        then: "indexes re-created"
        clazz.getClassIndexes().size() == 2
        // ignore check for remote test
        id1 == null || id(clazz.getClassIndex("FulltextIndexModel.defaults")) != id1
        id3 == null || id(clazz.getClassIndex("all_options")) != id3
        clazz.getClassIndex("FulltextIndexModel.defaults").getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        def custom = clazz.getClassIndex("all_options")
        custom.getType() == OClass.INDEX_TYPE.FULLTEXT.name()
        custom.getMetadata().field(FulltextIndexFieldExtension.INDEX_RADIX) == false
        custom.getMetadata().field(FulltextIndexFieldExtension.IGNORE_CHARS) == "'"
        custom.getMetadata().field(FulltextIndexFieldExtension.SEPARATOR_CHARS) == "!?"
        custom.getMetadata().field(FulltextIndexFieldExtension.MIN_WORD_LENGTH) == 5
        custom.getMetadata().field(FulltextIndexFieldExtension.STOP_WORDS) == ["of", "the"]
    }

    def "Check existing index with different fields"() {

        when: "index already exist with different fields"
        def clazz = db.getMetadata().getSchema().createClass(FulltextIndexModel)
        clazz.createProperty("defaults", OType.STRING)
        clazz.createProperty("other", OType.STRING)
        clazz.createIndex('FulltextIndexModel.defaults', OClass.INDEX_TYPE.FULLTEXT, "other")
        schemeInitializer.register(FulltextIndexModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    def "Check existing index with incompatible type"() {

        when: "index already exist with different type"
        def clazz = db.getMetadata().getSchema().createClass(FulltextIndexModel)
        clazz.createProperty("defaults", OType.STRING)
        clazz.createIndex('FulltextIndexModel.defaults', OClass.INDEX_TYPE.DICTIONARY, "defaults")
        schemeInitializer.register(FulltextIndexModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    private Object id(OIndex index) {
        return index.delegate instanceof OIndexRemote ? null : index.indexId
    }
}