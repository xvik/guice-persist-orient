package ru.vyarus.guice.persist.orient.finder.internal

import com.google.inject.Inject
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.finder.executor.DocumentFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.GraphFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.ObjectFinderExecutor
import ru.vyarus.guice.persist.orient.finder.result.ResultType
import ru.vyarus.guice.persist.orient.support.finder.InterfaceFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.TestFinderModule
import spock.guice.UseModules

import java.lang.reflect.Method

/**
 * @author Vyacheslav Rusakov 
 * @since 31.07.2014
 */
@UseModules(TestFinderModule)
class DescriptionFactoryTest extends AbstractTest {

    @Inject
    FinderDescriptorFactory factory;

    def "Check db type recognition"() {

        when: "object db method, detection by list generic"
        FinderDescriptor desc = lookup(InterfaceFinder.getMethod("selectAll"))
        then: "object provider recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        !desc.params.useNamedParameters
        desc.params.parametersIndex.length == 0
        !desc.isFunctionCall

        when: "object db method, detection by array type"
        desc = lookup(InterfaceFinder.getMethod("selectAllAsArray"))
        then: "object provider recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model

        when: "object db method, single return"
        desc = lookup(InterfaceFinder.getMethod("selectUnique"))
        then: "object provider recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model

        when: "document db method, detection by list generic"
        desc = lookup(InterfaceFinder.getMethod("selectAllAsDocument"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == ODocument

        when: "graph db method, detection by list generic"
        desc = lookup(InterfaceFinder.getMethod("selectAllAsVertex"))
        then: "graph provider recognized"
        desc.executor.class == GraphFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex

        when: "no return type, default document"
        desc = lookup(InterfaceFinder.getMethod("update"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.result.returnType == ResultType.PLAIN

        when: "primitive return type, default document"
        desc = lookup(InterfaceFinder.getMethod("updateWithCount"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.result.returnType == ResultType.PLAIN

        when: "primitive wrapper return type, default document"
        desc = lookup(InterfaceFinder.getMethod("updateWithCountObject"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.result.returnType == ResultType.PLAIN

        when: "list without generic"
        desc = lookup(InterfaceFinder.getMethod("selectAllNoType"))
        then: "document connection selected"
        desc.executor.class == DocumentFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
    }

    def "Check function recognition"() {

        when: "function call"
        FinderDescriptor desc = lookup(InterfaceFinder.getMethod("function"))
        then: "function recognized"
        desc.isFunctionCall

        when: "ambiguous function call"
        lookup(InterfaceFinder.getMethod("functionWrongDefinition"))
        then: "error"
        thrown(FinderDefinitionException)
    }

    def "Check params recognition"() {

        when: "positional parameters"
        FinderDescriptor desc = lookup(InterfaceFinder.getMethod("parametersPositional", String.class, String.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex == [0, 1]

        when: "named parameters"
        desc = lookup(InterfaceFinder.getMethod("parametersNamed", String.class, String.class))
        then: "recognized"
        desc.params.useNamedParameters
        desc.params.namedParametersIndex== ["name": 0, "nick":1]

        when: "positional parameters with warning, because of wrong @named use"
        desc = lookup(InterfaceFinder.getMethod("parametersPositionalWithWarning", String.class, String.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex== [0, 1]

        when: "named parameters incorrect declaration"
        lookup(InterfaceFinder.getMethod("parametersNames", String.class, String.class))
        then: "error"
        thrown(FinderDefinitionException)

        when: "named parameters duplicate declaration"
        lookup(InterfaceFinder.getMethod("parametersNamesDuplicateName", String.class, String.class))
        then: "error"
        thrown(FinderDefinitionException)

        when: "positional parameters with page definition"
        desc = lookup(InterfaceFinder.getMethod("parametersPaged", String.class, String.class, int.class, int.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex== [0, 1]
        desc.pagination.firstResultParamIndex==2
        desc.pagination.maxResultsParamIndex==3

        when: "positional parameters with page definition as objects"
        desc = lookup(InterfaceFinder.getMethod("parametersPagedObject", String.class, String.class, Long.class, Long.class))
        then: "recognized"
        !desc.params.useNamedParameters
        desc.params.parametersIndex== [0, 1]
        desc.pagination.firstResultParamIndex==2
        desc.pagination.maxResultsParamIndex==3

        when: "positional parameters with page definition as objects"
        lookup(InterfaceFinder.getMethod("parametersPagedDouble", String.class, String.class, int.class, int.class))
        then: "error"
        thrown(FinderDefinitionException)

        when: "positional parameters with page definition with wrong type"
        lookup(InterfaceFinder.getMethod("parametersPagedWrongType", String.class, String.class, String.class, int.class))
        then: "error"
        thrown(FinderDefinitionException)

        when: "positional parameters with page definition with wrong type"
        lookup(InterfaceFinder.getMethod("parametersPagedWrongType2", String.class, String.class, int.class, String.class))
        then: "error"
        thrown(FinderDefinitionException)
    }

    FinderDescriptor lookup(Method method) {
        template.doInTransaction({
            return factory.create(method)
        } as SpecificTxAction)
    }
}