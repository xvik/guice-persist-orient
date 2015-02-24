package ru.vyarus.guice.persist.orient.repository.command.ext.timeout;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension;

import javax.inject.Singleton;

/**
 * {@link Timeout} amend annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@Singleton
public class TimeoutAmendExtension implements AmendMethodExtension<CommandMethodDescriptor, Timeout>,
        CommandExtension<CommandMethodDescriptor> {

    public static final String KEY = TimeoutAmendExtension.class.getName();

    @Override
    public void handleAnnotation(final CommandMethodDescriptor descriptor, final Timeout annotation) {
        MethodDefinitionException.check(annotation.value() >= 0, "Timeout value can't be negative");
        descriptor.extDescriptors.put(KEY, new TimeoutDescriptor(annotation.value(), annotation.strategy()));
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        // not needed
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        final TimeoutDescriptor desc = (TimeoutDescriptor) descriptor.extDescriptors.get(KEY);
        if (desc.timeout > 0) {
            query.setTimeout(desc.timeout, desc.strategy);
        }
    }
}
