package ru.vyarus.guice.persist.orient.repository.command.descriptor

import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.executor.impl.ObjectRepositoryExecutor
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.support.ParametersCases
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class ParametersTest extends AbstractRepositoryDefinitionTest {

    def "Check parameters recognition"() {

        when: "positional parameters"
        CommandMethodDescriptor desc = lookup(ParametersCases.getMethod("parametersPositional", String.class, String.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex == [0, 1]

        when: "vararg parameter"
        desc = lookup(ParametersCases.getMethod("findWithVararg", String[]))
        then: "parameter recognized as array"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == List
        desc.params.parametersIndex == [0]

        when: "update with positional params"
        desc = lookup(ParametersCases.getMethod("updateWithParam", String.class, String.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex == [0, 1]

    }
}