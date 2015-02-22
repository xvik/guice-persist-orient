package ru.vyarus.guice.persist.orient.repository.command.descriptor

import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.executor.impl.ObjectRepositoryExecutor
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.support.OptionalCases
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class OptionalTest extends AbstractRepositoryDefinitionTest {

    def "Optional cases"() {

        when: "return guava optional"
        RepositoryMethodDescriptor desc = lookup(OptionalCases.getMethod("findGuavaOptional"))
        then: "optional recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.result.expectType == com.google.common.base.Optional

        when: "return guava optional"
        desc = lookup(OptionalCases.getMethod("emptyCollection"))
        then: "optional recognized"
        desc.executor.class == ObjectRepositoryExecutor
        desc.result.returnType == ResultType.PLAIN
        desc.result.entityType == Model
        desc.result.expectType == com.google.common.base.Optional
    }
}