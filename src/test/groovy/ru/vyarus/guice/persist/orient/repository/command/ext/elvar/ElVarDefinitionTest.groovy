package ru.vyarus.guice.persist.orient.repository.command.ext.elvar

import com.google.common.collect.Sets
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support.ObjVar
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support.VarDefinitionEnum
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support.ElVarsCases
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 22.09.2014
 */
@UseModules(RepositoryTestModule)
class ElVarDefinitionTest extends AbstractRepositoryDefinitionTest {

    def "Check vars recognition"() {

        when: "vars with definition"
        CommandMethodDescriptor desc = lookup(ElVarsCases.getMethod("findByField", String, String))
        ElVarDescriptor elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        elvars.parametersIndex == ['field': 0]
        Sets.newHashSet(elvars.values.get('field')) == ['name', 'nick'] as Set
        desc.params.parametersIndex == [1]

        when: "vars with two parameters"
        desc = lookup(ElVarsCases.getMethod("findByTwoFields", String, String, String, String))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        elvars.parametersIndex == ['field1': 0, 'field2': 1]
        Sets.newHashSet(elvars.values.get('field1')) == ['name', 'nick'] as Set
        Sets.newHashSet(elvars.values.get('field2')) == ['name', 'nick'] as Set
        desc.params.parametersIndex == [2, 3]

        when: "vars with enum parameter"
        desc = lookup(ElVarsCases.getMethod("findByEnumField", VarDefinitionEnum, String))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        elvars.parametersIndex == ['field': 0]
        Sets.newHashSet(elvars.values.get('field')) == [] as Set
        desc.params.parametersIndex == [1]

        when: "function vars"
        desc = lookup(ElVarsCases.getMethod("functionWithPlaceholder", String))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        elvars.parametersIndex == ['name': 0]
        Sets.newHashSet(elvars.values.get('name')) == [] as Set
        !desc.params.parametersIndex

        when: "function enum vars"
        desc = lookup(ElVarsCases.getMethod("functionWithEnum", VarDefinitionEnum))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        elvars.parametersIndex == ['name': 0]
        Sets.newHashSet(elvars.values.get('name')) == [] as Set
        !desc.params.parametersIndex

        when: "safe string"
        desc = lookup(ElVarsCases.getMethod("safeString", String))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        !desc.params.parametersIndex

        when: "int var"
        desc = lookup(ElVarsCases.getMethod("intVar", int.class))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        !desc.params.parametersIndex

        when: "integer var"
        desc = lookup(ElVarsCases.getMethod("integerVar", Integer.class))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        !desc.params.parametersIndex

        when: "object var"
        desc = lookup(ElVarsCases.getMethod("objVar", ObjVar.class))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars
        !desc.params.parametersIndex

        when: "class var"
        desc = lookup(ElVarsCases.getMethod("classVar", Class.class))
        elvars = desc.extDescriptors.get(ElVarParamExtension.KEY)
        then: "vars recognized"
        elvars.classParametersIndex.size() == 1
    }
}