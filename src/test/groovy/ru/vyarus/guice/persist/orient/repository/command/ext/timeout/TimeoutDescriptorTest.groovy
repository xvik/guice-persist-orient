package ru.vyarus.guice.persist.orient.repository.command.ext.timeout

import com.orientechnologies.orient.core.command.OCommandContext
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support.TimeoutCases
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@UseModules(RepositoryTestModule)
class TimeoutDescriptorTest extends AbstractRepositoryDefinitionTest {

    def "Check timeout annotation recognition"() {

        when: "analyze method with timeout"
        CommandMethodDescriptor desc = lookup(TimeoutCases.getMethod("all"))
        TimeoutDescriptor timeout = desc.extDescriptors.get(TimeoutAmendExtension.KEY)
        then: "recognized"
        timeout.timeout == 200
        timeout.strategy == OCommandContext.TIMEOUT_STRATEGY.EXCEPTION

        when: "analyze method with timeout and custom strategy"
        desc = lookup(TimeoutCases.getMethod("neverDone"))
        timeout = desc.extDescriptors.get(TimeoutAmendExtension.KEY)
        then: "recognized"
        timeout.timeout == 1
        timeout.strategy == OCommandContext.TIMEOUT_STRATEGY.RETURN

        when: "analyze method with zero timeout"
        desc = lookup(TimeoutCases.getMethod("noTimeout"))
        timeout = desc.extDescriptors.get(TimeoutAmendExtension.KEY)
        then: "recognized"
        timeout.timeout == 0

        when: "analyze method with bad timeout"
        lookup(TimeoutCases.getMethod("badTimeout"))
        then: "error"
        thrown(MethodDefinitionException)
    }
}