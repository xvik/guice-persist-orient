package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.drop

import com.orientechnologies.orient.core.metadata.schema.OClass
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class DropIndexTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.drop"
    }

    def "Check index drop"() {

        when: "no index exist yet"
        schemeInitializer.register(DropIndexModel)
        then: "nothing done"
        true

        when: "index exist"
        def clazz = db.getMetadata().getSchema().getClass(DropIndexModel)
        clazz.createIndex("test1", OClass.INDEX_TYPE.DICTIONARY, "foo")
        schemeInitializer.clearModelCache()
        schemeInitializer.register(DropIndexModel)
        then: "index dropped"
        clazz.getClassIndexes().isEmpty()
    }
}