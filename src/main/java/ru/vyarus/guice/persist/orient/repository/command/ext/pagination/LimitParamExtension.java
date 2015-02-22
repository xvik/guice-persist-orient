package ru.vyarus.guice.persist.orient.repository.command.ext.pagination;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;

import javax.inject.Singleton;

/**
 * {@link Limit} parameter annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
@Singleton
public class LimitParamExtension extends AbstractPaginationExtension<Limit>
        implements CommandExtension<CommandMethodDescriptor> {

    public static final String KEY = LimitParamExtension.class.getName();

    @Override
    protected String getKey() {
        return KEY;
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        // not needed
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        final Number limit = getValue(descriptor, arguments);
        if (limit == null || limit.intValue() == 0) {
            return;
        }
        query.setLimit(limit.intValue());
    }
}
