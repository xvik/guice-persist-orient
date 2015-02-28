package ru.vyarus.guice.persist.orient.repository.command.async;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery;
import ru.vyarus.guice.persist.orient.repository.command.core.AbstractCommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.ListenParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * {@link AsyncQuery} method extension.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@Singleton
public class AsyncQueryMethodExtension extends AbstractCommandExtension<CommandMethodDescriptor, AsyncQuery> {

    @Inject
    public AsyncQueryMethodExtension(final SpiService spiService) {
        super(spiService);
    }

    @Override
    public CommandMethodDescriptor createDescriptor(final DescriptorContext context, final AsyncQuery annotation) {
        final CommandMethodDescriptor descriptor = new CommandMethodDescriptor();
        descriptor.command = Strings.emptyToNull(annotation.value());

        analyzeElVars(descriptor, context);
        analyzeParameters(descriptor, context);

        // Listen extension will check that method is void
        check(descriptor.extDescriptors.get(ListenParamExtension.KEY) != null,
                "Required @%s parameter not defined", Listen.class.getSimpleName());
        return descriptor;
    }

    @Override
    protected OCommandRequest createQueryCommand(final CommandMethodDescriptor descriptor,
                                                 final SqlCommandDescriptor desc) {
        return new OSQLAsynchQuery(desc.command);
    }
}
