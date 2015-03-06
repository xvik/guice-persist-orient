package ru.vyarus.guice.persist.orient.repository.command.ext.lock.support.ext

import com.orientechnologies.orient.core.command.OCommandRequest
import com.orientechnologies.orient.core.command.OCommandRequestAbstract
import com.orientechnologies.orient.core.storage.OStorage
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension
import ru.vyarus.guice.persist.orient.db.util.Order

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
// extension executed last (after lock applied)
@Order(200)
class CheckLockExtension implements AmendMethodExtension<CommandMethodDescriptor, CheckLock>,
        CommandExtension<CommandMethodDescriptor> {

    static OStorage.LOCKING_STRATEGY expected

    @Override
    void handleAnnotation(CommandMethodDescriptor descriptor, CheckLock annotation) {
    }

    @Override
    void amendCommandDescriptor(SqlCommandDescriptor sql, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
    }

    @Override
    void amendCommand(OCommandRequest query, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {
        assert expected == ((OCommandRequestAbstract) query).getLockingStrategy()
    }
}
