package ru.vyarus.guice.persist.orient.repository.delegate

import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext
import ru.vyarus.guice.persist.orient.repository.delegate.method.TargetMethodAnalyzer
import ru.vyarus.guice.persist.orient.repository.delegate.support.Errors
import ru.vyarus.guice.persist.orient.repository.delegate.support.ErrorsDelegate
import ru.vyarus.guice.persist.orient.repository.delegate.support.Repository
import ru.vyarus.java.generics.resolver.GenericsResolver
import ru.vyarus.java.generics.resolver.context.GenericsContext
import spock.lang.Specification

import java.lang.reflect.Method


/**
 * Cases not valid, but fail will occur only on execution, because validation logic is in extensions.
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class ErrorCasesTest extends Specification {

    def "Check errors"() {

        when: "duplicate generic definition"
        lookup(Repository, Errors.getMethod('someth'), ErrorsDelegate, "someth")
        then:
        true

        when: "non existing generic definition"
        lookup(Repository, Errors.getMethod('someth'), ErrorsDelegate, "someth2")
        then:
        true

        when: "incorrect generic parameter type"
        lookup(Repository, Errors.getMethod('someth'), ErrorsDelegate, "someth3")
        then:
        true

        when: "duplicate instance definition"
        lookup(Repository, Errors.getMethod('someth'), ErrorsDelegate, "someth4")
        then:
        true

        when: "incorrect repository instance parameter type"
        lookup(Repository, Errors.getMethod('someth'), ErrorsDelegate, "someth5")
        then:
        true

        when: "duplicate connection param"
        lookup(Repository, Errors.getMethod('someth'), ErrorsDelegate, "someth6")
        then:
        true
    }

    Method lookup(Class iface, Method method, Class target, String hint = null) {
        GenericsContext generics = GenericsResolver.resolve(iface);
        TargetMethodAnalyzer.findDelegateMethod(
                new DescriptorContext(type: iface, method: method, generics: generics.type(method.getDeclaringClass())),
                target, hint)
    }
}