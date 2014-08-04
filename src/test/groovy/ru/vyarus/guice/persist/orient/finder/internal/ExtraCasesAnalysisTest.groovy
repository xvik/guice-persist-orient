package ru.vyarus.guice.persist.orient.finder.internal

import com.google.inject.Inject
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.finder.executor.GraphFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.ObjectFinderExecutor
import ru.vyarus.guice.persist.orient.finder.result.ResultType
import ru.vyarus.guice.persist.orient.support.finder.ExtraCasesFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

import java.lang.reflect.Method

/**
 * @author Vyacheslav Rusakov 
 * @since 05.08.2014
 */
@UseModules(AutoScanFinderTestModule)
class ExtraCasesAnalysisTest extends AbstractTest {

    @Inject
    FinderDescriptorFactory factory;

    def "Check db type recognition"() {

        when: "iterable return ahould be detected as collection"
        FinderDescriptor desc = lookup(ExtraCasesFinder.getMethod("selectAll"))
        then: "iterable recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.returnType == ResultType.COLLECTION
        desc.returnEntity == Model
        desc.expectType == Iterable
        !desc.useNamedParameters
        desc.parametersIndex.length == 0
        !desc.isFunctionCall

        when: "iterator return should be detected as collection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllIterator"))
        then: "iterator recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.returnType == ResultType.COLLECTION
        desc.returnEntity == Model
        desc.expectType == Iterator

        when: "iterator return should be detected as collection for graph connection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllVertex"))
        then: "iterator recognized"
        desc.executor.class == GraphFinderExecutor
        desc.returnType == ResultType.COLLECTION
        desc.returnEntity == Vertex
        desc.expectType == Iterator

        when: "iterable return should be detected as collection for graph connection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllVertexIterable"))
        then: "iterable recognized"
        desc.executor.class == GraphFinderExecutor
        desc.returnType == ResultType.COLLECTION
        desc.returnEntity == Vertex
        desc.expectType == Iterable

        when: "set collection override"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllAsSet"))
        then: "set recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.returnType == ResultType.COLLECTION
        desc.returnEntity == Model
        desc.expectType == HashSet

        when: "set collection override with graph connection"
        desc = lookup(ExtraCasesFinder.getMethod("selectAllAsSetGraph"))
        then: "set recognized"
        desc.executor.class == GraphFinderExecutor
        desc.returnType == ResultType.COLLECTION
        desc.returnEntity == Vertex
        desc.expectType == HashSet

        when: "vararg parameter"
        desc = lookup(ExtraCasesFinder.getMethod("findWithVararg", String[]))
        then: "parameter recognized as array"
        desc.executor.class == ObjectFinderExecutor
        desc.returnType == ResultType.COLLECTION
        desc.returnEntity == Model
        desc.expectType == List
        desc.parametersIndex == [0]
    }

    FinderDescriptor lookup(Method method) {
        template.doInTransaction({
            return factory.create(method)
        } as SpecificTxAction)
    }
}

