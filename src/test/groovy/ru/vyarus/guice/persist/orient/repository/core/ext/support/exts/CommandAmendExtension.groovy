package ru.vyarus.guice.persist.orient.repository.core.ext.support.exts

import com.orientechnologies.orient.core.command.OCommandRequest
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class CommandAmendExtension implements AmendMethodExtension<CommandMethodDescriptor, CmdAmend>,
        CommandExtension<CommandMethodDescriptor> {

    @Override
    void handleAnnotation(CommandMethodDescriptor descriptor, CmdAmend annotation) {

    }

    @Override
    void amendCommandDescriptor(SqlCommandDescriptor sql, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {

    }

    @Override
    void amendCommand(OCommandRequest query, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {

    }
}
