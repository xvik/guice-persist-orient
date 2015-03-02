package ru.vyarus.guice.persist.orient.repository.core.ext

import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.repository.core.ext.support.Mixin
import ru.vyarus.guice.persist.orient.repository.core.ext.support.Root
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.CmdAmend
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.DelegateAmend
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.UniversalAmend
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor
import spock.lang.Specification

import java.lang.annotation.Annotation

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class AmendSearchTest extends Specification {

    def "Check amend annotations search"() {

        when: "resolving amend annotations"
        List<Annotation> res = ExtUtils.findAmendAnnotations(Mixin.getMethod('selectAll'), Root, CommandMethodDescriptor)
        then: "correct annotations found"
        res.size() == 2
        res[0].annotationType() == CmdAmend
        res[1].annotationType() == UniversalAmend

        when: "resolving amend annotations"
        res = ExtUtils.findAmendAnnotations(Mixin.getMethod('delegate'), Root, DelegateMethodDescriptor)
        then: "correct annotations found"
        res.size() == 2
        res[0].annotationType() == DelegateAmend
        res[1].annotationType() == UniversalAmend

        when: "resolving incorrect amend annotations"
        ExtUtils.findAmendAnnotations(Mixin.getMethod('selectError'), Root, CommandMethodDescriptor)
        then: "error"
        thrown(MethodDefinitionException)
    }
}