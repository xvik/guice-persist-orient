package ru.vyarus.guice.persist.orient.repository.command.ext.placeholder

import com.google.common.collect.Sets
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.repository.command.ext.placeholder.support.RepositoryWithPlaceholders
import ru.vyarus.guice.persist.orient.repository.command.ext.placeholder.support.PlaceholdersEnum
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 22.09.2014
 */
@UseModules(RepositoryTestModule)
class PlaceholderDefinitionParseTest extends AbstractRepositoryDefinitionTest {

    def "Check placeholders recognition"() {

        when: "placeholder with definition"
        CommandMethodDescriptor desc = lookup(RepositoryWithPlaceholders.getMethod("findByField", String, String))
        PlaceholderDescriptor placeholders = desc.extDescriptors.get(PlaceholderParamExtension.KEY)
        then: "placeholder recognized"
        placeholders
        placeholders.parametersIndex == ['field': 0]
        Sets.newHashSet(placeholders.values.get('field')) == ['name', 'nick'] as Set
        desc.params.parametersIndex == [1]

        when: "placeholder with two parameters"
        desc = lookup(RepositoryWithPlaceholders.getMethod("findByTwoFields", String, String, String, String))
        placeholders = desc.extDescriptors.get(PlaceholderParamExtension.KEY)
        then: "placeholder recognized"
        placeholders
        placeholders.parametersIndex == ['field1': 0, 'field2': 1]
        Sets.newHashSet(placeholders.values.get('field1')) == ['name', 'nick'] as Set
        Sets.newHashSet(placeholders.values.get('field2')) == ['name', 'nick'] as Set
        desc.params.parametersIndex == [2, 3]

        when: "placeholder with enum parameter"
        desc = lookup(RepositoryWithPlaceholders.getMethod("findByEnumField", PlaceholdersEnum, String))
        placeholders = desc.extDescriptors.get(PlaceholderParamExtension.KEY)
        then: "placeholder recognized"
        placeholders
        placeholders.parametersIndex == ['field': 0]
        Sets.newHashSet(placeholders.values.get('field')) == [] as Set
        desc.params.parametersIndex == [1]

        when: "function placeholder"
        desc = lookup(RepositoryWithPlaceholders.getMethod("functionWithPlaceholder", String))
        placeholders = desc.extDescriptors.get(PlaceholderParamExtension.KEY)
        then: "placeholder recognized"
        placeholders
        placeholders.parametersIndex == ['name': 0]
        Sets.newHashSet(placeholders.values.get('name')) == [] as Set
        !desc.params.parametersIndex

        when: "function enum placeholder"
        desc = lookup(RepositoryWithPlaceholders.getMethod("functionWithPlaceholderEnum", PlaceholdersEnum))
        placeholders = desc.extDescriptors.get(PlaceholderParamExtension.KEY)
        then: "placeholder recognized"
        placeholders
        placeholders.parametersIndex == ['name': 0]
        Sets.newHashSet(placeholders.values.get('name')) == [] as Set
        !desc.params.parametersIndex
    }

    def "Check placeholder definition errors"() {

        when: "enum with definition"
        lookup(RepositoryWithPlaceholders.getMethod("functionWithPlaceholderEnumErr", PlaceholdersEnum))
        then: "error"
        thrown(IllegalStateException)

        when: "default for not defined placeholder"
        lookup(RepositoryWithPlaceholders.getMethod("findByFieldErrDefaults", String, String))
        then: "error"
        thrown(IllegalStateException)

        when: "bad placeholder parameter type"
        lookup(RepositoryWithPlaceholders.getMethod("findByFieldErrType", Integer, String))
        then: "error"
        thrown(IllegalStateException)

        when: "not all placeholders covered by parameter"
        lookup(RepositoryWithPlaceholders.getMethod("findByFieldsErrParam", String, String, String))
        then: "error"
        thrown(MethodDefinitionException)

        when: "no placeholders, but placeholder param defined"
        lookup(RepositoryWithPlaceholders.getMethod("findByFieldErrNoPlaceholders", String, String))
        then: "error"
        thrown(IllegalStateException)

        when: "too many placeholder param definitions"
        lookup(RepositoryWithPlaceholders.getMethod("findByFieldErrTooMany", String, String, String))
        then: "error"
        thrown(MethodDefinitionException)

        when: "duplicate placeholder param definition"
        lookup(RepositoryWithPlaceholders.getMethod("findByFieldErrDuplicateParamDefinition", String, String, String))
        then: "error"
        thrown(IllegalStateException)

        when: "duplicate placeholder defaults definition"
        lookup(RepositoryWithPlaceholders.getMethod("findByFieldErrDuplicateDefaultsDefinition", String, String))
        then: "error"
        thrown(IllegalStateException)
    }
}