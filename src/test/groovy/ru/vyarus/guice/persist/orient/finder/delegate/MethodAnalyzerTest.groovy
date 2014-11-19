package ru.vyarus.guice.persist.orient.finder.delegate

import ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException
import ru.vyarus.guice.persist.orient.finder.internal.delegate.method.MethodDescriptor
import ru.vyarus.guice.persist.orient.finder.internal.delegate.method.MethodDescriptorAnalyzer
import ru.vyarus.guice.persist.orient.support.finder.delegate.*
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.java.generics.resolver.GenericsResolver
import ru.vyarus.java.generics.resolver.context.GenericsContext
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * @author Vyacheslav Rusakov 
 * @since 22.10.2014
 */
class MethodAnalyzerTest extends Specification {

    def "Check implemented mixin resolution"() {

        when: "searching method directly implementing finder interface (using finder method name)"
        MethodDescriptor desc = lookup(Finder, ru.vyarus.guice.persist.orient.support.finder.delegate.Mixin.getMethod('count'), MixinImpl)
        then:
        desc.method.getName() == 'count'

        when: "searching method directly implementing finder interface (using finder method name)"
        desc = lookup(Finder, ru.vyarus.guice.persist.orient.support.finder.delegate.Mixin.getMethod('add', int, int), MixinImpl)
        then:
        desc.method.getName() == 'add'

        when: "searching method directly implementing finder interface (using finder method name)"
        desc = lookup(Finder, ru.vyarus.guice.persist.orient.support.finder.delegate.Mixin.getMethod('merge', Object, int), MixinImpl)
        then:
        desc.method.getName() == 'merge'
    }

    def "Check indirect reference with hint"() {

        when: "searching method with different name but with method hint"
        MethodDescriptor desc = lookup(Finder, Finder.getMethod('countIndirect'), MixinImpl, 'count')
        then:
        desc.method.getName() == 'count'

        when: "searching method with different name but with method hint"
        desc = lookup(Finder, Finder.getMethod('addIndirect', int, int), MixinImpl, 'add')
        then:
        desc.method.getName() == 'add'

        when: "searching method with different name but with method hint"
        desc = lookup(Finder, Finder.getMethod('mergeIndirect', Model, int), MixinImpl, 'merge')
        then:
        desc.method.getName() == 'merge'
    }

    def "Check indirect reference"() {

        when: "searching method with different name"
        MethodDescriptor desc = lookup(Finder, Finder.getMethod('count'), MixinImpl)
        then:
        desc.method.getName() == 'count'

        when: "searching method with different name"
        desc = lookup(Finder, Finder.getMethod('addIndirect', int, int), MixinImpl)
        then:
        desc.method.getName() == 'add'

        when: "searching method with different name"
        desc = lookup(Finder, Finder.getMethod('mergeIndirect', Model, int), MixinImpl)
        then:
        desc.method.getName() == 'merge'
    }

    def "Check complex delegation"() {

        when: "search method with additional parameters"
        MethodDescriptor desc = lookup(Finder, ComplexFinder.getMethod('someth'), ComplexFinderDelegate)
        then:
        desc.method.getName() == 'someth'
        desc.typeParams.size() == 1
        desc.instancePosition == 1

        when: "search method with additional parameters"
        desc = lookup(Finder, ComplexFinder.getMethod('add', int.class, int.class), ComplexFinderDelegate)
        then:
        desc.method.getName() == 'add'
        desc.typeParams.size() == 1
    }

    def "Check errors"() {

        when: "duplicate generic definition"
        lookup(Finder, Errors.getMethod('someth'), ErrorsDelegate, "someth")
        then:
        thrown(FinderDefinitionException)

        when: "non existing generic definition"
        lookup(Finder, Errors.getMethod('someth'), ErrorsDelegate, "someth2")
        then:
        thrown(FinderDefinitionException)

        when: "incorrect generic parameter type"
        lookup(Finder, Errors.getMethod('someth'), ErrorsDelegate, "someth3")
        then:
        thrown(FinderDefinitionException)

        when: "duplicate instance definition"
        lookup(Finder, Errors.getMethod('someth'), ErrorsDelegate, "someth4")
        then:
        thrown(FinderDefinitionException)

        when: "incorrect finder instance parameter type"
        lookup(Finder, Errors.getMethod('someth'), ErrorsDelegate, "someth5")
        then:
        thrown(FinderDefinitionException)

        when: "duplicate connection param"
        lookup(Finder, Errors.getMethod('someth'), ErrorsDelegate, "someth6")
        then:
        thrown(FinderDefinitionException)
    }

    MethodDescriptor lookup(Class iface, Method method, Class target, String hint = null) {
        GenericsContext generics = GenericsResolver.resolve(iface);
        return MethodDescriptorAnalyzer.analyzeMethod(method, target, hint, generics.type(method.getDeclaringClass()), iface)
    }
}