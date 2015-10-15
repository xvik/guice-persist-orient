package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.ext

import com.orientechnologies.orient.core.command.OCommandRequest
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension
import ru.vyarus.guice.persist.orient.db.util.Order

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
// extension executed last (after fetch applied)
@Order(200)
class CheckCommandExtension implements AmendMethodExtension<CommandMethodDescriptor, CheckCommand>,
        CommandExtension<CommandMethodDescriptor> {

    static String expectedPlan

    @Override
    void handleAnnotation(CommandMethodDescriptor descriptor, CheckCommand annotation) {
    }

    @Override
    void amendCommandDescriptor(SqlCommandDescriptor sql, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
    }

    @Override
    void amendCommand(OCommandRequest query, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
        def idx = query.text.indexOf('FETCHPLAN');
        def plan = null
        if (idx >0){
           plan = query.text.substring(idx+'FETCHPLAN'.length()).trim()
        }
        assert expectedPlan == plan
    }
}
