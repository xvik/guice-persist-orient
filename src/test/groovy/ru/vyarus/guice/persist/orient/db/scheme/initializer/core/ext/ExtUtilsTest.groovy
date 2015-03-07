package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ExtendedModel
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ExtendedModel2
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.FieldExt
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.TypeExt
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov 
 * @since 07.03.2015
 */
class ExtUtilsTest extends Specification {

    def "Check extensions search"() {

        when: "searching for type extensions"
        def res = ExtUtils.findTypeAnnotations(ExtendedModel)
        then: "found"
        res.size() == 1
        res[0] instanceof TypeExt

        when: "searching for type extensions"
        res = ExtUtils.findTypeAnnotations(ExtendedModel2)
        then: "not found"
        res.isEmpty()

        when: "searching for field extensions"
        res = ExtUtils.findFieldAnnotations(ExtendedModel.getDeclaredField("foo"))
        then: "extension found"
        res.size() == 1
        res[0] instanceof FieldExt

        when: "searching for field extensions"
        res = ExtUtils.findFieldAnnotations(ExtendedModel.getDeclaredField("bar"))
        then: "not found"
        res.isEmpty()

        when: "searching for field extensions"
        res = ExtUtils.findFieldAnnotations(ExtendedModel2.getDeclaredField("other"))
        then: "extension found"
        res.size() == 1
        res[0] instanceof FieldExt

        when: "searching for field extensions"
        res = ExtUtils.findFieldAnnotations(ExtendedModel2.getDeclaredField("boo"))
        then: "not found"
        res.isEmpty()
    }
}