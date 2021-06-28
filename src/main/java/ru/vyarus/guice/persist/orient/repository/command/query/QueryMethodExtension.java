package ru.vyarus.guice.persist.orient.repository.command.query;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import ru.vyarus.guice.persist.orient.repository.command.core.AbstractCommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Query method extension. Defined by {@link Query} annotation.
 *
 * @author Vyacheslav Rusakov
 * @since 02.02.2015
 */
@Singleton
public class QueryMethodExtension extends AbstractCommandExtension<CommandMethodDescriptor, Query> {

    @Inject
    public QueryMethodExtension(final SpiService spiService) {
        super(spiService);
    }

    @Override
    public CommandMethodDescriptor createDescriptor(final DescriptorContext context, final Query annotation) {
        final CommandMethodDescriptor descriptor = new CommandMethodDescriptor();
        descriptor.command = Strings.emptyToNull(annotation.value());
        descriptor.connectionHint = annotation.connection();
        descriptor.returnCollectionHint = annotation.returnAs();

        analyzeElVars(descriptor, context);
        analyzeParameters(descriptor, context);
        return descriptor;
    }

    @Override
    protected OCommandRequest createQueryCommand(final CommandMethodDescriptor descriptor,
                                                 final SqlCommandDescriptor desc) {
        final String query = desc.command;
        final boolean isQuery = query.toLowerCase().startsWith("select");
        final OCommandRequest command;
        if (isQuery) {
            command = new OSQLSynchQuery<>(query);
        } else {
            command = new OCommandSQL(query);
        }
        return command;
    }
}
