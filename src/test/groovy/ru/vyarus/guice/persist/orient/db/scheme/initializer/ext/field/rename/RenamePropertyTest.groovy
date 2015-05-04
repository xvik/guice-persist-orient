package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.rename

import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 07.03.2015
 */
class RenamePropertyTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.rename"
    }

    def "Check field rename"() {

        when: "renaming property"
        SchemeUtils.command(db, "create class RenamePropertyModel")
        SchemeUtils.command(db, "create property RenamePropertyModel.foo string")
        schemeInitializer.register(RenamePropertyModel)
        then: "property renamed"
        db.getMetadata().getSchema().getClass('RenamePropertyModel').getProperty("bar") != null
        db.getMetadata().getSchema().getClass('RenamePropertyModel').getProperty("foo") == null

        when: "renaming already renamed property"
        schemeInitializer.clearModelCache()
        schemeInitializer.register(RenamePropertyModel)
        then: "nothing happen"
        db.getMetadata().getSchema().getClass('RenamePropertyModel').getProperty("bar") != null
        db.getMetadata().getSchema().getClass('RenamePropertyModel').getProperty("foo") == null
    }

    def "Check property rename when class not exist"() {

        when: "rename property when class not exist"
        schemeInitializer.register(RenamePropertyModel)
        then: "nothing happen"
        true
    }

    def "Check property rename when from not exist"() {

        when: "rename property when old not exist"
        schemeInitializer.register(RenamePropertyModel)
        schemeInitializer.clearModelCache()
        schemeInitializer.register(RenamePropertyModel)
        then: "nothing happen"
        true
    }


    def "Check both models already exist"() {

        when: "rename property when both exist"
        SchemeUtils.command(db, "create class RenamePropertyModel")
        SchemeUtils.command(db, "create property RenamePropertyModel.bar string")
        SchemeUtils.command(db, "create property RenamePropertyModel.foo string")
        schemeInitializer.clearModelCache()
        schemeInitializer.register(RenamePropertyModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    def "Check bad definition"() {

        when: "old name is empty"
        schemeInitializer.register(BadPropertyModel)
        then: "error"
        thrown(SchemeInitializationException)

        when: "old name is the same"
        schemeInitializer.register(BadPropertyModel2)
        then: "error"
        thrown(SchemeInitializationException)
    }
}