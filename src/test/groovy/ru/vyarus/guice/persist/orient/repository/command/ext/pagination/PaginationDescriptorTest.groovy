package ru.vyarus.guice.persist.orient.repository.command.ext.pagination

import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class PaginationDescriptorTest extends AbstractRepositoryDefinitionTest {

    def "Check pagination"() {

        when: "positional parameters with page definition"
        CommandMethodDescriptor desc = lookup(PaginationCases.getMethod("parametersPaged", String.class, String.class, int.class, int.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex == [0, 1]
        desc.extDescriptors.get(SkipParamExtension.KEY) == 2
        desc.extDescriptors.get(LimitParamExtension.KEY) == 3

        when: "positional parameters with page definition as objects"
        desc = lookup(PaginationCases.getMethod("parametersPagedObject", String.class, String.class, Long.class, Long.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex == [0, 1]
        desc.extDescriptors.get(SkipParamExtension.KEY) == 2
        desc.extDescriptors.get(LimitParamExtension.KEY) == 3

        when: "positional parameters with page definition as objects"
        lookup(PaginationCases.getMethod("parametersPagedDouble", String.class, String.class, int.class, int.class))
        then: "error"
        thrown(IllegalStateException)

        when: "positional parameters with page definition with wrong type"
        lookup(PaginationCases.getMethod("parametersPagedWrongType", String.class, String.class, String.class, int.class))
        then: "error"
        thrown(IllegalStateException)

        when: "positional parameters with page definition with wrong type"
        lookup(PaginationCases.getMethod("parametersPagedWrongType2", String.class, String.class, int.class, String.class))
        then: "error"
        thrown(IllegalStateException)
    }
}