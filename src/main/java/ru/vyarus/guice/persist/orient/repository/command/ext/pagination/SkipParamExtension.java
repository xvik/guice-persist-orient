package ru.vyarus.guice.persist.orient.repository.command.ext.pagination;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException;
import ru.vyarus.guice.persist.orient.db.util.Order;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;

import javax.inject.Singleton;

/**
 * {@link Skip} parameter annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
@Singleton
// executed before default extensions, because it modifies query string
@Order(-10)
public class SkipParamExtension extends AbstractPaginationExtension<Skip>
        implements CommandExtension<CommandMethodDescriptor> {

    public static final String KEY = SkipParamExtension.class.getName();

    @Override
    protected String getKey() {
        return KEY;
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        final Number skip = getValue(descriptor, arguments);
        if (skip == null || skip.intValue() <= 0) {
            return;
        }
        final String query = sql.command;
        MethodDefinitionException.check(query.toLowerCase().startsWith("select"),
                "@Skip may be used only for select queries");
        sql.command = query + " SKIP " + skip;
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not needed
    }
}
