package ru.vyarus.guice.persist.orient.repository.command.descriptor

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.executor.impl.DocumentRepositoryExecutor
import ru.vyarus.guice.persist.orient.repository.core.executor.impl.GraphRepositoryExecutor
import ru.vyarus.guice.persist.orient.repository.core.executor.impl.ObjectRepositoryExecutor
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.support.DbRecognitionCases
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class DbTypeRecognitionTest extends AbstractRepositoryDefinitionTest {

    def "Check db type recognition"() {

        when: "object db method, detection by list generic"
        CommandMethodDescriptor desc = lookup(DbRecognitionCases.getMethod("selectAll"))
        then: "object provider recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        !desc.params.useNamedParameters
        desc.params.parametersIndex.length == 0

        when: "object db method, detection by array type"
        desc = lookup(DbRecognitionCases.getMethod("selectAllAsArray"))
        then: "object provider recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model

        when: "object db method, single return"
        desc = lookup(DbRecognitionCases.getMethod("selectUnique"))
        then: "object provider recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model

        when: "document db method, detection by list generic"
        desc = lookup(DbRecognitionCases.getMethod("selectAllAsDocument"))
        then: "document provider recognized"
        desc.executor.class == DocumentRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == ODocument

        when: "graph db method, detection by list generic"
        desc = lookup(DbRecognitionCases.getMethod("selectAllAsVertex"))
        then: "graph provider recognized"
        desc.executor.class == GraphRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex

        when: "graph db method, detection by list generic with derivative type"
        desc = lookup(DbRecognitionCases.getMethod("selectAllAsOrientVertex"))
        then: "graph provider recognized"
        desc.executor.class == GraphRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == OrientVertex

        when: "no return type, default document"
        desc = lookup(DbRecognitionCases.getMethod("update"))
        then: "document provider recognized"
        desc.executor.class == DocumentRepositoryExecutor
        desc.result.returnType == ResultType.VOID

        when: "primitive return type, default document"
        desc = lookup(DbRecognitionCases.getMethod("updateWithCount"))
        then: "document provider recognized"
        desc.executor.class == DocumentRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN

        when: "primitive wrapper return type, default document"
        desc = lookup(DbRecognitionCases.getMethod("updateWithCountObject"))
        then: "document provider recognized"
        desc.executor.class == DocumentRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN

        when: "list without generic"
        desc = lookup(DbRecognitionCases.getMethod("selectAllNoType"))
        then: "document connection selected"
        desc.executor.class == DocumentRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
    }

    def "Check additional recognition cases"() {

        when: "iterable return should be detected as collection"
        RepositoryMethodDescriptor desc = lookup(DbRecognitionCases.getMethod("selectAllIterable"))
        then: "iterable recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == Iterable
        !desc.params.useNamedParameters
        desc.params.parametersIndex.length == 0

        when: "iterator return should be detected as collection"
        desc = lookup(DbRecognitionCases.getMethod("selectAllIterator"))
        then: "iterator recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == Iterator

        when: "iterator return should be detected as collection for graph connection"
        desc = lookup(DbRecognitionCases.getMethod("selectAllVertex"))
        then: "iterator recognized"
        desc.executor.class == GraphRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex
        desc.result.expectType == Iterator

        when: "iterable return should be detected as collection for graph connection"
        desc = lookup(DbRecognitionCases.getMethod("selectAllVertexIterable"))
        then: "iterable recognized"
        desc.executor.class == GraphRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex
        desc.result.expectType == Iterable

        when: "set collection override"
        desc = lookup(DbRecognitionCases.getMethod("selectAllAsSet"))
        then: "set recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == HashSet

        when: "set collection override with graph connection"
        desc = lookup(DbRecognitionCases.getMethod("selectAllAsSetGraph"))
        then: "set recognized"
        desc.executor.class == GraphRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex
        desc.result.expectType == HashSet

        when: "document overridden for object connection"
        desc = lookup(DbRecognitionCases.getMethod("documentOverride"))
        then: "select object connection"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == ODocument
        desc.result.expectType == List
    }
}