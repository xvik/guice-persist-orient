package ru.vyarus.guice.persist.orient.repository.command.ext.pagination;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.db.util.Order;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException;

import javax.inject.Singleton;

/**
 * {@link Limit} parameter annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
@Singleton
// executed before default extensions, because it modifies query string
@Order(-9)
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
        final Number limit = getValue(descriptor, arguments);
        if (limit == null || limit.intValue() == 0) {
            return;
        }
        final String query = sql.command;
        MethodDefinitionException.check(query.toLowerCase().startsWith("select"),
                "@Limit may be used only for select queries");
        sql.command = query + " LIMIT " + limit;
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not needed
    }
}
