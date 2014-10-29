package ru.vyarus.guice.persist.orient.finder.internal

import ru.vyarus.guice.persist.orient.finder.executor.ObjectFinderExecutor
import ru.vyarus.guice.persist.orient.finder.result.ResultType
import ru.vyarus.guice.persist.orient.support.finder.inheritance.*
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 17.10.2014
 */
@UseModules(AutoScanFinderTestModule)
class HierarchyCasesAnalysisTest extends AbstractFinderDefinitionTest {

    def "Check generic finder interfaces"() {

        when: "analyze finder wih generic placeholder"
        FinderDescriptor desc = lookup(PowerFinder, DeepBaseFinder.getMethod("selectOne"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['K': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(PowerFinder, BaseFinder1.getMethod("selectAll"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(PowerFinder, BaseFinder2.getMethod("findByField", String, Object))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(PowerFinder, DeepBaseFinder.getMethod("selectCustom"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['K': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(PowerFinder, DeepBaseFinder.getMethod("selectOptional"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.expectType == com.google.common.base.Optional
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['K': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(PowerFinder, DeepBaseFinder.getMethod("selectAllIterator"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['K': Model.simpleName]

        when: "analyze finder wih complex generic"
        desc = lookup(PowerFinder, ComplexGeneric.getMethod("selectAllComplex"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih complex generic"
        desc = lookup(PowerFinder, ComplexGeneric.getMethod("selectAllComplex"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih complex generic"
        desc = lookup(PowerFinder, ComplexGeneric2.getMethod("selectCustomArray"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['I': Model.simpleName]

        when: "analyze finder wih complex generic"
        desc = lookup(PowerFinder, ComplexGeneric2.getMethod("selectCustomList"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['I': Model.simpleName]
    }

    def "Check generic finder beans"() {

        when: "analyze finder wih generic placeholder"
        FinderDescriptor desc = lookup(BeanPowerFinder, BaseBeanFinder.getMethod("findByField", String, Object))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['K': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(BeanPowerFinder, Lvl2BaseBeanFinder.getMethod("selectOne"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(BeanPowerFinder, Lvl2BaseBeanFinder.getMethod("selectAll"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(BeanPowerFinder, BaseBeanFinder.getMethod("selectCustom"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['K': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(BeanPowerFinder, Lvl2BaseBeanFinder.getMethod("selectOptional"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(BeanPowerFinder, Lvl2BaseBeanFinder.getMethod("selectOptional"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.expectType == com.google.common.base.Optional
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]

        when: "analyze finder wih generic placeholder"
        desc = lookup(BeanPowerFinder, Lvl2BaseBeanFinder.getMethod("selectAllIterator"))
        then: "recognized"
        desc.executor.class == ObjectFinderExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.expectType == Iterator
        desc.result.entityType == Model
        desc.placeholders.genericParameters == ['T': Model.simpleName]
    }
}