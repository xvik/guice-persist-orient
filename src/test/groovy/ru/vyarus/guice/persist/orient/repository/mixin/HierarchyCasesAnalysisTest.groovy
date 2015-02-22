package ru.vyarus.guice.persist.orient.repository.mixin

import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.executor.impl.ObjectRepositoryExecutor
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType
import ru.vyarus.guice.persist.orient.repository.mixin.support.*
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 17.10.2014
 */
@UseModules(RepositoryTestModule)
class HierarchyCasesAnalysisTest extends AbstractRepositoryDefinitionTest {

    def "Check generic repository interfaces"() {

        when: "analyze repository wih generic placeholder"
        CommandMethodDescriptor desc = lookup(PowerRepository, DeepBaseRepository.getMethod("selectOne"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.el.directValues == ['K': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(PowerRepository, BaseRepository1.getMethod("selectAll"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(PowerRepository, BaseRepository2.getMethod("findByField", String, Object))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository with generic placeholder"
        desc = lookup(PowerRepository, DeepBaseRepository.getMethod("selectCustom"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model
        desc.el.directValues == ['K': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(PowerRepository, DeepBaseRepository.getMethod("selectOptional"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.expectType == com.google.common.base.Optional
        desc.result.entityType == Model
        desc.el.directValues == ['K': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(PowerRepository, DeepBaseRepository.getMethod("selectAllIterator"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['K': Model.simpleName]

        when: "analyze repository wih complex generic"
        desc = lookup(PowerRepository, ComplexGeneric.getMethod("selectAllComplex"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository wih complex generic"
        desc = lookup(PowerRepository, ComplexGeneric.getMethod("selectAllComplex"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository wih complex generic"
        desc = lookup(PowerRepository, ComplexGeneric2.getMethod("selectCustomArray"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model
        desc.el.directValues == ['I': Model.simpleName]

        when: "analyze repository wih complex generic"
        desc = lookup(PowerRepository, ComplexGeneric2.getMethod("selectCustomList"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['I': Model.simpleName]
    }

    def "Check generic repository beans"() {

        when: "analyze repository wih generic placeholder"
        CommandMethodDescriptor desc = lookup(BeanPowerRepository, BaseBeanRepository.getMethod("findByField", String, Object))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['K': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(BeanPowerRepository, Lvl2BaseBeanRepository.getMethod("selectOne"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(BeanPowerRepository, Lvl2BaseBeanRepository.getMethod("selectAll"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(BeanPowerRepository, BaseBeanRepository.getMethod("selectCustom"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.ARRAY
        desc.result.entityType == Model
        desc.el.directValues == ['K': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(BeanPowerRepository, Lvl2BaseBeanRepository.getMethod("selectOptional"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(BeanPowerRepository, Lvl2BaseBeanRepository.getMethod("selectOptional"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.expectType == com.google.common.base.Optional
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]

        when: "analyze repository wih generic placeholder"
        desc = lookup(BeanPowerRepository, Lvl2BaseBeanRepository.getMethod("selectAllIterator"))
        then: "recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.COLLECTION
        desc.result.expectType == Iterator
        desc.result.entityType == Model
        desc.el.directValues == ['T': Model.simpleName]
    }
}