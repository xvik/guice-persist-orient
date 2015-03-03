package ru.vyarus.guice.persist.orient.repository.core.ext

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.OCommandRequest
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService
import ru.vyarus.guice.persist.orient.repository.core.ext.support.SampleRepo
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 25.02.2015
 */
@UseModules(RepositoryTestModule)
class GlobalExtensionsTest extends AbstractTest {

    @Inject
    AmendExtensionsService exts
    @Inject
    SampleRepo repository

    def "Check global extensions"() {

        def ext = new CommandExtension<CommandMethodDescriptor>() {
            int executed = 0

            @Override
            void amendCommandDescriptor(SqlCommandDescriptor sql, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
            }

            @Override
            void amendCommand(OCommandRequest query, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
                executed++
            }
        }
        exts.addGlobalExtension(ext)

        when: "call method with global extension active"
        repository.select()
        then: "extension called"
        ext.executed == 1

        when: "remove extension and call method"
        exts.removeGlobalExtension(ext)
        repository.select()
        then: "extension called, because method descriptor already cached"
        ext.executed == 2

        when: "call different method when extension removed"
        repository.select2()
        then: "extension not executed, because it's not registered in time of descriptor creation"
        ext.executed == 2
    }
}