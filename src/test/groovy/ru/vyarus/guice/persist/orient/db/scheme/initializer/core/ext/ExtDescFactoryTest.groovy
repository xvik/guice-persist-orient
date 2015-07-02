package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.TestModel1
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.TestModel2
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.bad.BadFieldExtModel
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.bad.BadTypeExtModel
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.FieldExtension2
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.FieldExtension3
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.TypeExtension2
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.TypeExtension3
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
@UseModules(DefaultModule)
class ExtDescFactoryTest extends AbstractTest {

    @Inject
    ExtensionsDescriptorFactory factory

    def "Check extensions resolution and sorting"() {

        when: "inspecting simple model"
        def res = factory.resolveExtensions(TestModel1)
        then: "extensions resolved and ordered"
        res.type.size() == 3
        res.type[0].extension instanceof TypeExtension2
        res.type[1].extension instanceof TypeExtension3
        res.fields.keySet().size() == 1
        res.fields.get("foo").size() == 3
        res.fields.get("foo")[0].extension instanceof FieldExtension3
        res.fields.get("foo")[1].extension instanceof FieldExtension2

        when: "inspecting extended model"
        res = factory.resolveExtensions(TestModel2)
        then: "extensions resolved and ordered"
        res.type.size() == 2
        res.type[0].extension instanceof TypeExtension2
        res.type[1].extension instanceof TypeExtension3
        res.fields.isEmpty()
    }

    def "Check error extensions"() {

        when: "resolving bad type extension"
        factory.resolveExtensions(BadTypeExtModel)
        then: "error"
        thrown(SchemeInitializationException)

        when: "resolving bad field extension"
        factory.resolveExtensions(BadFieldExtModel)
        then: "error"
        thrown(SchemeInitializationException)
    }
}