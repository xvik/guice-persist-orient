package ru.vyarus.guice.persist.orient.repository.command.ext.var

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.OCommandRequest
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 25.02.2015
 */
@UseModules([RepositoryTestModule, BootstrapModule])
class VarExtensionTest extends AbstractTest {

    @Inject
    AmendExtensionsService exts;
    @Inject
    VarCases dao

    def "Check variables"() {
        List<String> expected = null
        exts.addGlobalExtension(new CommandExtension<CommandMethodDescriptor>() {
            @Override
            void amendCommandDescriptor(SqlCommandDescriptor sql, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
            }

            @Override
            void amendCommand(OCommandRequest query, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
                expected?.each({ assert query.getContext().getVariable(it) != null })
            }
        })

        when: "check extension validation works"
        expected = ['1']
        dao.string('dsfsd')
        then: "fail - validation works"
        thrown(RepositoryException)

        when: "using script variable"
        expected = ['tst']
        def res = dao.string("name1")
        then: "variable visible in script context"
        res == 'name1'

        when: "using string variable"
        expected = ['tst']
        res = dao.string("name1")
        then: "selected"
        res == 'name1'

        when: "using list variable"
        expected = ['tst']
        res = dao.list(["name1", "name3"])
        then: "selected"
        res as Set == ["name1", "name3"] as Set

        when: "variable name is empty"
        dao.empty("fgg")
        then: "error"
        thrown(RepositoryException)

        when: "duplicate variable name"
        dao.duplicate("fgg", "sdsd")
        then: "error"
        thrown(RepositoryException)
    }
}