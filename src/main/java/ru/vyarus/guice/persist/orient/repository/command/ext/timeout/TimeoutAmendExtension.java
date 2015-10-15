package ru.vyarus.guice.persist.orient.repository.command.ext.timeout;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.db.util.Order;
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
// executed before default extensions, because it modifies query string
@Order(-5)
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
        final TimeoutDescriptor desc = (TimeoutDescriptor) descriptor.extDescriptors.get(KEY);
        if (desc.timeout <= 0) {
            return;
        }
        final String query = sql.command;
        MethodDefinitionException.check(query.toLowerCase().startsWith("select"),
                "@Timeout may be used only for select queries");
        sql.command = query + " TIMEOUT " + desc.timeout + " " + desc.strategy.name();
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not needed
    }
}
