package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.rename

import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 07.03.2015
 */
class RenameFromModelTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.rename"
    }

    def "Check model rename"() {

        when: "renaming class"
        SchemeUtils.command(db, "create class OriginalModel")
        schemeInitializer.register(RenameModel)
        then: "class renamed"
        db.getMetadata().getSchema().getClass('OriginalModel') == null

        when: "renaming already renamed class"
        schemeInitializer.clearModelCache()
        schemeInitializer.register(RenameModel)
        then: "nothing happen"
        db.getMetadata().getSchema().getClass('OriginalModel') == null
    }

    def "Check class rename when from not exist"() {

        when: "rename model when old not exist"
        schemeInitializer.register(RenameModel)
        then: "nothing happen"
        true
    }

    def "Check both models already exist"() {

        when: "rename model when both exist"
        // register new model
        schemeInitializer.register(RenameModel)
        // register old model
        SchemeUtils.command(db, "create class OriginalModel")
        schemeInitializer.clearModelCache()
        schemeInitializer.register(RenameModel)
        then: "error"
        thrown(SchemeInitializationException)
    }

    def "Check bad definition"() {

        when: "old name is empty"
        schemeInitializer.register(BadRenameModel)
        then: "error"
        thrown(SchemeInitializationException)

        when: "old name is the same"
        schemeInitializer.register(BadRenameModel2)
        then: "error"
        thrown(SchemeInitializationException)
    }
}