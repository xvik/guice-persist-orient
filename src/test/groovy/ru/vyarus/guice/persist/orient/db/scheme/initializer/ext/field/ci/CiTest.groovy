package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci

import com.orientechnologies.orient.core.collate.OCaseInsensitiveCollate
import com.orientechnologies.orient.core.collate.ODefaultCollate
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * @author Vyacheslav Rusakov 
 * @since 09.06.2015
 */
class CiTest extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci"
    }

    def "Check ci"() {

        when: "creating class"
        schemeInitializer.register(CiModel)
        then: "case insensitive set"
        db.metadata.schema.getClass(CiModel).getProperty("ci").collate.name == OCaseInsensitiveCollate.NAME
        db.metadata.schema.getClass(CiModel).getProperty("nonci").collate.name == ODefaultCollate.NAME
    }

    def "Check ci unset"() {

        when: "ci flag set and should be removed"
        OClass clazz = db.metadata.schema.createClass(CiModel)
        clazz.createProperty("ci", OType.STRING).setCollate(ODefaultCollate.NAME)
        clazz.createProperty("nonci", OType.STRING).setCollate(OCaseInsensitiveCollate.NAME)
        schemeInitializer.register(CiModel)
        then: "ci correctly set"
        db.metadata.schema.getClass(CiModel).getProperty("ci").collate.name == OCaseInsensitiveCollate.NAME
        db.metadata.schema.getClass(CiModel).getProperty("nonci").collate.name == ODefaultCollate.NAME
    }

}