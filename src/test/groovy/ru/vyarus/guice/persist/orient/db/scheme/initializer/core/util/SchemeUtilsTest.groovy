package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.ext.ExtendedModel
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.ext.ExtendedModel2
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.ext.FieldExt
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.ext.TypeExt
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.hierarchy.Base
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.hierarchy.Derived
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
class SchemeUtilsTest extends Specification {

    def "Check hierarchy parsing"() {

        when: "parsing simple class"
        def res = SchemeUtils.resolveHierarchy(Base)
        then: "one class returned"
        res == [Base]

        when: "parsing complex class"
        res = SchemeUtils.resolveHierarchy(Derived)
        then: "heirarchy parsed"
        res == [Derived, Base]
    }

    def "Check extensions search"() {

        when: "searching for type extensions"
        def res = SchemeUtils.findTypeAnnotations(ExtendedModel)
        then: "found"
        res.size() == 1
        res[0] instanceof TypeExt

        when: "searching for type extensions"
        res = SchemeUtils.findTypeAnnotations(ExtendedModel2)
        then: "not found"
        res.isEmpty()

        when: "searching for field extensions"
        res = SchemeUtils.findFieldAnnotations(ExtendedModel.getDeclaredField("foo"))
        then: "extension found"
        res.size() == 1
        res[0] instanceof FieldExt

        when: "searching for field extensions"
        res = SchemeUtils.findFieldAnnotations(ExtendedModel.getDeclaredField("bar"))
        then: "not found"
        res.isEmpty()

        when: "searching for field extensions"
        res = SchemeUtils.findFieldAnnotations(ExtendedModel2.getDeclaredField("other"))
        then: "extension found"
        res.size() == 1
        res[0] instanceof FieldExt

        when: "searching for field extensions"
        res = SchemeUtils.findFieldAnnotations(ExtendedModel2.getDeclaredField("boo"))
        then: "not found"
        res.isEmpty()
    }
}