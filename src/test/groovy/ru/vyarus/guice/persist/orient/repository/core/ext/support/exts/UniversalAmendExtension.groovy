package ru.vyarus.guice.persist.orient.repository.core.ext.support.exts

import com.orientechnologies.orient.core.command.OCommandRequest
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateExtension
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor

/**
 * Universal amend extension (may be applied to different method types).
 * The main trick is RepositoryMethodDescriptor declaration, so extension would pass compatibility check
 * with any method. Next extension implements extension interfaces for both methods.
 *
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class UniversalAmendExtension implements AmendMethodExtension<RepositoryMethodDescriptor, UniversalAmend>,
        CommandExtension<CommandMethodDescriptor>, DelegateExtension<DelegateMethodDescriptor> {

    @Override
    void handleAnnotation(RepositoryMethodDescriptor descriptor, UniversalAmend annotation) {

    }

    @Override
    void amendCommandDescriptor(SqlCommandDescriptor sql, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {

    }

    @Override
    void amendCommand(OCommandRequest query, CommandMethodDescriptor descriptor, Object instance, Object... arguments) {

    }

    @Override
    void amendParameters(DelegateMethodDescriptor descriptor, Object[] targetArgs, Object instance, Object... arguments) {

    }
}
