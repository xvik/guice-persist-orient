package ru.vyarus.guice.persist.orient.repository.command.ext.named

import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class NamedParamsDescriptorTest extends AbstractRepositoryDefinitionTest {

    def "Check named params recognition"() {

        when: "named parameters"
        CommandMethodDescriptor desc = lookup(NamedParamsCases.getMethod("parametersNamed", String.class, String.class))
        then: "recognized"
        desc.params.useNamedParameters
        desc.params.namedParametersIndex == ["name": 0, "nick": 1]

        when: "positional parameters with warning, because of wrong @named use"
        lookup(NamedParamsCases.getMethod("parametersPositionalWithOrdinal", String.class, String.class))
        then: "recognized"
        thrown(MethodDefinitionException)

        when: "named parameters incorrect declaration"
        lookup(NamedParamsCases.getMethod("parametersNames", String.class, String.class))
        then: "error"
        thrown(MethodDefinitionException)

        when: "named parameters duplicate declaration"
        lookup(NamedParamsCases.getMethod("parametersNamesDuplicateName", String.class, String.class))
        then: "error"
        thrown(IllegalStateException)
    }
}