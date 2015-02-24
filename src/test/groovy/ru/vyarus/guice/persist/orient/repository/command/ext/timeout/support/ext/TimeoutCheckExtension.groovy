package ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support.ext

import com.orientechnologies.orient.core.command.OCommandRequest
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.command.ext.timeout.TimeoutDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension
import ru.vyarus.guice.persist.orient.repository.core.util.Order

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
        assert expected?.timeout == query.getTimeoutTime()
        assert expected?.strategy == query.getTimeoutStrategy()
    }
}
