package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.notnull

import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class NotNullTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.notnull"
    }

    def "Check notnull"() {

        when: "creating class"
        schemeInitializer.register(NotNullModel)
        then: "notnull set"
        db.getMetadata().getSchema().getClass(NotNullModel).getProperty("notnull").isNotNull()
        !db.getMetadata().getSchema().getClass(NotNullModel).getProperty("nullable").isNotNull()
    }

    def "Check notnull unset"() {

        when: "notnull flag set and should be removed"
        OClass clazz = db.getMetadata().getSchema().createClass(NotNullModel)
        clazz.createProperty("notnull", OType.STRING).setNotNull(false)
        clazz.createProperty("nullable", OType.STRING).setNotNull(true)
        schemeInitializer.register(NotNullModel)
        then: "notnull correctly set"
        db.getMetadata().getSchema().getClass(NotNullModel).getProperty("notnull").isNotNull()
        !db.getMetadata().getSchema().getClass(NotNullModel).getProperty("nullable").isNotNull()
    }
}