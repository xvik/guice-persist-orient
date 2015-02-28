package ru.vyarus.guice.persist.orient.repository.core.util

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.core.util.support.MethodToStringCases
import ru.vyarus.guice.persist.orient.repository.core.util.support.UsualRepository
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import java.lang.reflect.Method

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(RepositoryTestModule)
class RepositoryUtilsTest extends AbstractTest {

    @Inject
    UsualRepository repository

    def "Check instance class resolution"() {

        expect: "original class correctly resolved from proxies"
        RepositoryUtils.resolveRepositoryClass(repository) == UsualRepository
    }

    def "Check method to string"() {

        when: "no args method"
        def res = lookup("noargs")
        then: "ok"
        res == "MethodToStringCases#noargs()"

        when: "simple args method"
        res = lookup("simple", Object, Class)
        then: "ok"
        res == "MethodToStringCases#simple(Object, Class)"

        when: "primitive args method"
        res = lookup("primitive", int, long)
        then: "ok"
        res == "MethodToStringCases#primitive(int, long)"

        when: "generic args method"
        res = lookup("generic", Object, Model)
        then: "ok"
        res == "MethodToStringCases#generic(<T>, <K>)"

        when: "array args method"
        res = lookup("array", Object[])
        then: "ok"
        res == "MethodToStringCases#array(Object[])"

        when: "vararg method"
        res = lookup("vararg", Object[])
        then: "ok"
        res == "MethodToStringCases#vararg(Object[])"
    }

    String lookup(String method, Class... args) {
        return RepositoryUtils.methodToString(MethodToStringCases.getMethod(method, args))
    }
}