package ru.vyarus.guice.persist.orient.repository.core.ext

import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.ext.pagination.LimitParamExtension
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtCompatibilityUtils
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.GenericParamExtension
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class CompatibilityChecksTest extends Specification {

    def "Check compatibility check"() {

        when: "extension compatible"
        def res = ExtCompatibilityUtils.isCompatible(LimitParamExtension, MethodParamExtension, CommandMethodDescriptor)
        then: "compatible"
        res

        when: "extension incompatible"
        res = ExtCompatibilityUtils.isCompatible(LimitParamExtension, MethodParamExtension, DelegateMethodDescriptor)
        then: "incompatible"
        !res

        when: "extension compatible"
        res = ExtCompatibilityUtils.isCompatible(LimitParamExtension, CommandExtension, CommandMethodDescriptor)
        then: "compatible"
        res

        when: "extension incompatible"
        res = ExtCompatibilityUtils.isCompatible(LimitParamExtension, CommandExtension, DelegateMethodDescriptor)
        then: "incompatible"
        !res
    }

    def "Param compatibility check"() {

        when: "parameter extension is compatible"
        ExtCompatibilityUtils.checkParamExtensionCompatibility(CommandMethodDescriptor, LimitParamExtension)
        then: "check ok"

        when: "parameter extension is incompatible"
        ExtCompatibilityUtils.checkParamExtensionCompatibility(CommandMethodDescriptor, GenericParamExtension)
        then: "error thrown"
        thrown(MethodDefinitionException)
    }
}