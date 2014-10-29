package ru.vyarus.guice.persist.orient.finder.internal

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.finder.executor.GraphFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.ObjectFinderExecutor
import ru.vyarus.guice.persist.orient.finder.result.ResultType
import ru.vyarus.guice.persist.orient.support.finder.ExtraCasesFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 05.08.2014
 */
@UseModules(AutoScanFinderTestModule)
class ExtraCasesAnalysisTest extends AbstractFinderDefinitionTest {

    def "Check db type recognition"() {

        when: "iterable return should be detected as collection"
        FinderDescriptor desc = lookup(ExtraCasesFinder.getMethod("selectAll"))
        then: "iterable recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == Iterable
        !desc.params.useNamedParameters
        desc.params.parametersIndex.length == 0
        !desc.isFunctionCall

        when: "iterator return should be detected as collection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllIterator"))
        then: "iterator recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == Iterator

        when: "iterator return should be detected as collection for graph connection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllVertex"))
        then: "iterator recognized"
        desc.executor.class == GraphFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex
        desc.result.expectType == Iterator

        when: "iterable return should be detected as collection for graph connection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllVertexIterable"))
        then: "iterable recognized"
        desc.executor.class == GraphFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex
        desc.result.expectType == Iterable

        when: "set collection override"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllAsSet"))
        then: "set recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == HashSet

        when: "set collection override with graph connection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllAsSetGraph"))
        then: "set recognized"
        desc.executor.class == GraphFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Vertex
        desc.result.expectType == HashSet

        when: "vararg parameter"
        desc = lookup(ExtraCasesFinder.getMethod("findWithVararg", String[]))
        then: "parameter recognized as array"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.result.expectType == List
        desc.params.parametersIndex == [0]

        when: "document overridden for object connection"
        desc = lookup(ExtraCasesFinder.getMethod("documentOverride"))
        then: "select object connection"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == ODocument
        desc.result.expectType == List
    }

    def "Optional cases"() {

        when: "return guava optional"
        FinderDescriptor desc = lookup(ExtraCasesFinder.getMethod("findGuavaOptional"))
        then: "optional recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.result.expectType == com.google.common.base.Optional

//        when: "return jdk optional"
//        desc = lookup(ExtraCasesFinder.getMethod("findJdkOptional"))
//        then: "optional recognized"
//        desc.executor.class == ObjectFinderExecutor
//        desc.result.returnType == ResultType.PLAIN
//        desc.result.entityType == Model
//        desc.result.expectType == Optional
    }
}

