package ru.vyarus.guice.persist.orient.finder.internal

import com.google.inject.Inject
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.finder.executor.DocumentFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.GraphFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.ObjectFinderExecutor
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
        desc.returnType == FinderDescriptor.ReturnType.COLLECTION
        desc.returnEntity == Model
        !desc.useNamedParameters
        desc.parametersIndex.length == 0
        !desc.isFunctionCall

        when: "object db method, detection by array type"
        desc = lookup(InterfaceFinder.getMethod("selectAllAsArray"))
        then: "object provider recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.ARRAY
        desc.returnEntity == Model

        when: "object db method, single return"
        desc = lookup(InterfaceFinder.getMethod("selectUnique"))
        then: "object provider recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.PLAIN
        desc.returnEntity == Model

        when: "document db method, detection by list generic"
        desc = lookup(InterfaceFinder.getMethod("selectAllAsDocument"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.COLLECTION
        desc.returnEntity == ODocument

        when: "graph db method, detection by list generic"
        desc = lookup(InterfaceFinder.getMethod("selectAllAsVertex"))
        then: "graph provider recognized"
        desc.executor.class == GraphFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.COLLECTION
        desc.returnEntity == Vertex

        when: "no return type, default document"
        desc = lookup(InterfaceFinder.getMethod("update"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.PLAIN

        when: "primitive return type, default document"
        desc = lookup(InterfaceFinder.getMethod("updateWithCount"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.PLAIN

        when: "primitive wrapper return type, default document"
        desc = lookup(InterfaceFinder.getMethod("updateWithCountObject"))
        then: "document provider recognized"
        desc.executor.class == DocumentFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.PLAIN

        when: "list without generic"
        desc = lookup(InterfaceFinder.getMethod("selectAllNoType"))
        then: "document connection selected"
        desc.executor.class == DocumentFinderExecutor
        desc.returnType == FinderDescriptor.ReturnType.COLLECTION
    }

    def "Check function recognition"() {

        when: "function call"
        FinderDescriptor desc = lookup(InterfaceFinder.getMethod("function"))
        then: "function recognized"
        desc.isFunctionCall

        when: "ambiguous function call"
        lookup(InterfaceFinder.getMethod("functionWrongDefinition"))
        then: "error"
        thrown(IllegalStateException)
    }

    def "Check params recognition"() {

        when: "positional parameters"
        FinderDescriptor desc = lookup(InterfaceFinder.getMethod("parametersPositional", String.class, String.class))
        then: "recognized"
        !desc.useNamedParameters
        desc.parametersIndex == [0, 1]

        when: "named parameters"
        desc = lookup(InterfaceFinder.getMethod("parametersNamed", String.class, String.class))
        then: "recognized"
        desc.useNamedParameters
        desc.namedParametersIndex== ["name": 0, "nick":1]

        when: "positional parameters with warning, because of wrong @named use"
        desc = lookup(InterfaceFinder.getMethod("parametersPositionalWithWarning", String.class, String.class))
        then: "recognized"
        !desc.useNamedParameters
        desc.parametersIndex== [0, 1]

        when: "named parameters incorrect declaration"
        lookup(InterfaceFinder.getMethod("parametersNames", String.class, String.class))
        then: "error"
        thrown(NullPointerException)

        when: "named parameters duplicate declaration"
        lookup(InterfaceFinder.getMethod("parametersNamesDuplicateName", String.class, String.class))
        then: "error"
        thrown(IllegalStateException)

        when: "positional parameters with page definition"
        desc = lookup(InterfaceFinder.getMethod("parametersPaged", String.class, String.class, int.class, int.class))
        then: "recognized"
        !desc.useNamedParameters
        desc.parametersIndex== [0, 1]
        desc.firstResultParamIndex==2
        desc.maxResultsParamIndex==3

        when: "positional parameters with page definition as objects"
        desc = lookup(InterfaceFinder.getMethod("parametersPagedObject", String.class, String.class, Long.class, Long.class))
        then: "recognized"
        !desc.useNamedParameters
        desc.parametersIndex== [0, 1]
        desc.firstResultParamIndex==2
        desc.maxResultsParamIndex==3

        when: "positional parameters with page definition as objects"
        lookup(InterfaceFinder.getMethod("parametersPagedDouble", String.class, String.class, int.class, int.class))
        then: "error"
        thrown(IllegalArgumentException)

        when: "positional parameters with page definition with wrong type"
        lookup(InterfaceFinder.getMethod("parametersPagedWrongType", String.class, String.class, String.class, int.class))
        then: "error"
        thrown(IllegalArgumentException)

        when: "positional parameters with page definition with wrong type"
        lookup(InterfaceFinder.getMethod("parametersPagedWrongType2", String.class, String.class, int.class, String.class))
        then: "error"
        thrown(IllegalArgumentException)
    }

    FinderDescriptor lookup(Method method) {
        template.doInTransaction({
            return factory.create(method)
        } as SpecificTxAction)
    }
}