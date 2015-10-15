package ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support.ext

import com.orientechnologies.orient.core.command.OCommandContext
import com.orientechnologies.orient.core.command.OCommandRequest
import ru.vyarus.guice.persist.orient.db.util.Order
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.command.ext.timeout.TimeoutDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
// extension executed last (after timeout applied)
@Order(200)
class TimeoutCheckExtension implements AmendMethodExtension<CommandMethodDescriptor, TimeoutCheck>,
        CommandExtension<CommandMethodDescriptor> {

    static TimeoutDescriptor expected

    @Override
    void handleAnnotation(CommandMethodDescriptor descriptor, TimeoutCheck annotation) {
    }

    @Override
    void amendCommandDescriptor(SqlCommandDescriptor sql, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
    }

    @Override
    void amendCommand(OCommandRequest query, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
        def idx = query.text.indexOf("TIMEOUT");
        long timeout
        OCommandContext.TIMEOUT_STRATEGY strategy = null
        if (idx > 0) {
            def split = query.text.substring(idx + "TIMEOUT".length()).trim().split(' ')
            timeout = split[0] as long
            strategy = OCommandContext.TIMEOUT_STRATEGY.valueOf(split[1])
        }
        assert expected?.timeout == timeout
        assert expected?.strategy == strategy
    }
}
