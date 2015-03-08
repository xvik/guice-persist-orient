package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.readonly

import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class ReadonlyTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.readonly"
    }

    def "Check readonly"() {

        when: "creating class"
        schemeInitializer.register(ReadonlyModel)
        then: "readonly set"
        db.getMetadata().getSchema().getClass(ReadonlyModel).getProperty("immutable").isReadonly()
        !db.getMetadata().getSchema().getClass(ReadonlyModel).getProperty("mutable").isReadonly()
    }

    def "Check readonly unset"() {

        when: "readonly flag set and should be removed"
        OClass clazz = db.getMetadata().getSchema().createClass(ReadonlyModel)
        clazz.createProperty("immutable", OType.STRING).setReadonly(false)
        clazz.createProperty("mutable", OType.STRING).setReadonly(true)
        schemeInitializer.register(ReadonlyModel)
        then: "readonly correctly set"
        db.getMetadata().getSchema().getClass(ReadonlyModel).getProperty("immutable").isReadonly()
        !db.getMetadata().getSchema().getClass(ReadonlyModel).getProperty("mutable").isReadonly()
    }
}