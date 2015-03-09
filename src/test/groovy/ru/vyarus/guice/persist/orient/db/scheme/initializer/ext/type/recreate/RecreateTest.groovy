package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.recreate

import com.orientechnologies.orient.core.metadata.schema.OType
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class RecreateTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.recreate"
    }

    def "Check scheme recreation"() {

        when: "register scheme first time"
        schemeInitializer.register(RecreateModel)
        then: "ok"

        when: "updating scheme"
        def clazz = db.getMetadata().getSchema().getClass(RecreateModel)
        clazz.createProperty("bar", OType.STRING)
        schemeInitializer.clearModelCache()
        schemeInitializer.register(RecreateModel)
        db.getMetadata().getSchema().synchronizeSchema()
        clazz = db.getMetadata().getSchema().getClass(RecreateModel)
        then: "model dropped and created"
        clazz.getProperty("bar") == null
    }
}