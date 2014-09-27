package ru.vyarus.guice.persist.orient.finder.internal

import com.google.common.collect.Sets
import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.FinderWithPlaceholders
import ru.vyarus.guice.persist.orient.support.finder.PlaceholdersEnum
import ru.vyarus.guice.persist.orient.support.modules.TestFinderModule
import spock.guice.UseModules

import java.lang.reflect.Method

/**
 * @author Vyacheslav Rusakov 
 * @since 22.09.2014
 */
@UseModules(TestFinderModule)
class PlaceholderDefinitionParseTest extends AbstractTest {
    @Inject
    FinderDescriptorFactory factory;

    def "Check placeholders recognition"() {

        when: "placeholder with definition"
        FinderDescriptor desc = lookup(FinderWithPlaceholders.getMethod("findByField", String, String))
        then: "placeholder recognized"
        desc.placeholders
        desc.placeholders.parametersIndex == ['field': 0]
        Sets.newHashSet(desc.placeholders.values.get('field')) == ['name', 'nick'] as Set
        desc.params.parametersIndex == [1]

        when: "placeholder with two parameters"
        desc = lookup(FinderWithPlaceholders.getMethod("findByTwoFields", String, String, String, String))
        then: "placeholder recognized"
        desc.placeholders
        desc.placeholders.parametersIndex == ['field1': 0, 'field2': 1]
        Sets.newHashSet(desc.placeholders.values.get('field1')) == ['name', 'nick'] as Set
        Sets.newHashSet(desc.placeholders.values.get('field2')) == ['name', 'nick'] as Set
        desc.params.parametersIndex == [2, 3]

        when: "placeholder with enum parameter"
        desc = lookup(FinderWithPlaceholders.getMethod("findByEnumField", PlaceholdersEnum, String))
        then: "placeholder recognized"
        desc.placeholders
        desc.placeholders.parametersIndex == ['field': 0]
        Sets.newHashSet(desc.placeholders.values.get('field')) == [] as Set
        desc.params.parametersIndex == [1]

        when: "function placeholder"
        desc = lookup(FinderWithPlaceholders.getMethod("functionWithPlaceholder", String))
        then: "placeholder recognized"
        desc.placeholders
        desc.placeholders.parametersIndex == ['name': 0]
        Sets.newHashSet(desc.placeholders.values.get('name')) == [] as Set
        !desc.params.parametersIndex

        when: "function enum placeholder"
        desc = lookup(FinderWithPlaceholders.getMethod("functionWithPlaceholderEnum", PlaceholdersEnum))
        then: "placeholder recognized"
        desc.placeholders
        desc.placeholders.parametersIndex == ['name': 0]
        Sets.newHashSet(desc.placeholders.values.get('name')) == [] as Set
        !desc.params.parametersIndex
    }

    def "Check placeholder definition errors"() {

        when: "enum with definition"
        lookup(FinderWithPlaceholders.getMethod("functionWithPlaceholderEnumErr", PlaceholdersEnum))
        then: "error"
        thrown(FinderDefinitionException)

        when: "default for not defined placeholder"
        lookup(FinderWithPlaceholders.getMethod("findByFieldErrDefaults", String, String))
        then: "error"
        thrown(FinderDefinitionException)

        when: "bad placeholder parameter type"
        lookup(FinderWithPlaceholders.getMethod("findByFieldErrType", Integer, String))
        then: "error"
        thrown(FinderDefinitionException)

        when: "not all placeholders covered by parameter"
        lookup(FinderWithPlaceholders.getMethod("findByFieldsErrParam", String, String, String))
        then: "error"
        thrown(FinderDefinitionException)

        when: "no placeholders, but placeholder param defined"
        lookup(FinderWithPlaceholders.getMethod("findByFieldErrNoPlaceholders", String, String))
        then: "error"
        thrown(FinderDefinitionException)

        when: "too many placeholder param definitions"
        lookup(FinderWithPlaceholders.getMethod("findByFieldErrTooMany", String, String, String))
        then: "error"
        thrown(FinderDefinitionException)

        when: "duplicate placeholder in query"
        lookup(FinderWithPlaceholders.getMethod("findByFieldErrDuplicateDefinition", String, String))
        then: "error"
        thrown(FinderDefinitionException)

        when: "duplicate placeholder param definition"
        lookup(FinderWithPlaceholders.getMethod("findByFieldErrDuplicateParamDefinition", String, String, String))
        then: "error"
        thrown(FinderDefinitionException)

        when: "duplicate placeholder defaults definition"
        lookup(FinderWithPlaceholders.getMethod("findByFieldErrDuplicateDefaultsDefinition", String, String))
        then: "error"
        thrown(FinderDefinitionException)
    }

    FinderDescriptor lookup(Method method) {
        template.doInTransaction({
            return factory.create(method)
        } as SpecificTxAction)
    }
}