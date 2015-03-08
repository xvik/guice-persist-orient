package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.mandatory

import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class MandatoryTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.mandatory"
    }

    def "Check mandatory"() {

        when: "creating class"
        schemeInitializer.register(MandatoryModel)
        then: "mandatory set"
        db.getMetadata().getSchema().getClass(MandatoryModel).getProperty("required").isMandatory()
        !db.getMetadata().getSchema().getClass(MandatoryModel).getProperty("any").isMandatory()
    }

    def "Check mandatory unset"() {

        when: "mandatory flag set and should be removed"
        OClass clazz = db.getMetadata().getSchema().createClass(MandatoryModel)
        clazz.createProperty("required", OType.STRING).setMandatory(false)
        clazz.createProperty("any", OType.STRING).setMandatory(true)
        schemeInitializer.register(MandatoryModel)
        then: "mandatory correctly set"
        db.getMetadata().getSchema().getClass(MandatoryModel).getProperty("required").isMandatory()
        !db.getMetadata().getSchema().getClass(MandatoryModel).getProperty("any").isMandatory()
    }
}