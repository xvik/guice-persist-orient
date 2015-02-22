package ru.vyarus.guice.persist.orient.repository.delegate

import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext
import ru.vyarus.guice.persist.orient.repository.delegate.method.TargetMethodAnalyzer
import ru.vyarus.guice.persist.orient.repository.delegate.support.*
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

        when: "searching method directly implementing repository interface (using repository method name)"
        Method method = lookup(Repository, Mixin.getMethod('count'), MixinImpl)
        then:
        method.getName() == 'count'

        when: "searching method directly implementing repository interface (using repository method name)"
        method = lookup(Repository, Mixin.getMethod('add', int, int), MixinImpl)
        then:
        method.getName() == 'add'

        when: "searching method directly implementing repository interface (using repository method name)"
        method = lookup(Repository, Mixin.getMethod('merge', Object, int), MixinImpl)
        then:
        method.getName() == 'merge'
    }

    def "Check indirect reference with hint"() {

        when: "searching method with different name but with method hint"
        Method method = lookup(Repository, Repository.getMethod('countIndirect'), MixinImpl, 'count')
        then:
        method.getName() == 'count'

        when: "searching method with different name but with method hint"
        method = lookup(Repository, Repository.getMethod('addIndirect', int, int), MixinImpl, 'add')
        then:
        method.getName() == 'add'

        when: "searching method with different name but with method hint"
        method = lookup(Repository, Repository.getMethod('mergeIndirect', Model, int), MixinImpl, 'merge')
        then:
        method.getName() == 'merge'
    }

    def "Check indirect reference"() {

        when: "searching method with different name"
        Method method = lookup(Repository, Repository.getMethod('count'), MixinImpl)
        then:
        method.getName() == 'count'

        when: "searching method with different name"
        method = lookup(Repository, Repository.getMethod('addIndirect', int, int), MixinImpl)
        then:
        method.getName() == 'add'

        when: "searching method with different name"
        method = lookup(Repository, Repository.getMethod('mergeIndirect', Model, int), MixinImpl)
        then:
        method.getName() == 'merge'
    }

    def "Check complex delegation"() {

        when: "search method with additional parameters"
        Method method = lookup(Repository, ComplexRepository.getMethod('someth'), ComplexRepositoryDelegate)
        then:
        method.getName() == 'someth'

        when: "search method with additional parameters"
        method = lookup(Repository, ComplexRepository.getMethod('add', int.class, int.class), ComplexRepositoryDelegate)
        then:
        method.getName() == 'add'
    }

    Method lookup(Class iface, Method method, Class target, String hint = null) {
        GenericsContext generics = GenericsResolver.resolve(iface);
        TargetMethodAnalyzer.findDelegateMethod(
                new DescriptorContext(type: iface, method: method, generics: generics.type(method.getDeclaringClass())),
                target, hint)
    }
}