package ru.vyarus.guice.persist.orient.repository.command.ext.dynamicparams

import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 27.02.2015
 */
@UseModules(RepositoryTestModule)
class DynamicParamsDefinitionTest extends AbstractRepositoryDefinitionTest {

    def "Check dynamic params definition"() {

        when: "positional list params"
        CommandMethodDescriptor desc = lookup(DynamicParamsCases.getMethod("positionalList", List))
        DynamicParamsDescriptor dvars = desc.extDescriptors.get(DynamicParamsExtension.KEY)
        then: "vars recognized"
        dvars
        !dvars.named

        when: "positional array params"
        desc = lookup(DynamicParamsCases.getMethod("positionalArray", String[]))
        dvars = desc.extDescriptors.get(DynamicParamsExtension.KEY)
        then: "vars recognized"
        dvars
        !dvars.named

        when: "positional vararg params"
        desc = lookup(DynamicParamsCases.getMethod("positionalVararg", String[]))
        dvars = desc.extDescriptors.get(DynamicParamsExtension.KEY)
        then: "vars recognized"
        dvars
        !dvars.named

        when: "named params"
        desc = lookup(DynamicParamsCases.getMethod("namedMap", Map))
        dvars = desc.extDescriptors.get(DynamicParamsExtension.KEY)
        then: "vars recognized"
        dvars
        dvars.named

        when: "mix positional"
        desc = lookup(DynamicParamsCases.getMethod("mixPositional", String, String[]))
        dvars = desc.extDescriptors.get(DynamicParamsExtension.KEY)
        then: "vars recognized"
        dvars
        !dvars.named

        when: "mix named"
        desc = lookup(DynamicParamsCases.getMethod("mixNamed", String, Map))
        dvars = desc.extDescriptors.get(DynamicParamsExtension.KEY)
        then: "vars recognized"
        dvars
        dvars.named
    }

    def "Check error cases"() {

        when: "wrong params type"
        lookup(DynamicParamsCases.getMethod("wrongType", Object))
        then: "error"
        thrown(IllegalStateException)

        when: "duplicate declaration"
        lookup(DynamicParamsCases.getMethod("duplicate", Object[], Object[]))
        then: "error"
        thrown(IllegalStateException)
    }
}