package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan

import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.FetchPlanCases
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@UseModules(RepositoryTestModule)
class FetchPlanDescriptorTest extends AbstractRepositoryDefinitionTest {

    def "Check fetchplan param recognition"() {

        when: "analyze method with fetchplan parameter with default"
        CommandMethodDescriptor desc = lookup(FetchPlanCases.getMethod("selectBasket", String.class))
        FetchPlanDescriptor plan = desc.extDescriptors.get(FetchPlanParamExtension.KEY)
        then: "recognized"
        plan.defPlan == '*:0'
        plan.position == 0

        when: "analyze method with fetchplan parameter without default"
        desc = lookup(FetchPlanCases.getMethod("selectBasketNoDefault", String.class))
        plan = desc.extDescriptors.get(FetchPlanParamExtension.KEY)
        then: "recognized"
        !plan.defPlan
        plan.position == 0

        when: "analyze method with duplicate fetchplan parameter"
        lookup(FetchPlanCases.getMethod("duplicate", String.class, String.class))
        then: "error"
        thrown(IllegalStateException)

        when: "analyze method with bad fetchplan parameter type"
        lookup(FetchPlanCases.getMethod("badType", Integer.class))
        then: "error"
        thrown(IllegalStateException)
    }
}