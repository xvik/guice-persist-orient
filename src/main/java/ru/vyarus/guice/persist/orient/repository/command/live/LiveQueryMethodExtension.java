package ru.vyarus.guice.persist.orient.repository.command.live;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.query.OLiveQuery;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import ru.vyarus.guice.persist.orient.repository.command.core.AbstractCommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.ListenParamExtension;
import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * {@link LiveQuery} method extension.
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
@Singleton
public class LiveQueryMethodExtension extends AbstractCommandExtension<CommandMethodDescriptor, LiveQuery> {

    @Inject
    public LiveQueryMethodExtension(final SpiService spiService) {
        super(spiService);
    }

    @Override
    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    public CommandMethodDescriptor createDescriptor(final DescriptorContext context, final LiveQuery annotation) {
        final CommandMethodDescriptor descriptor = new CommandMethodDescriptor();
        String command = Preconditions.checkNotNull(Strings.emptyToNull(annotation.value()), "Query not specified");
        // annotation already indicate live and no need to duplicate it in query
        if (!command.toLowerCase().startsWith("live")) {
            command = "live " + command;
        }
        descriptor.command = command;

        analyzeElVars(descriptor, context);
        analyzeParameters(descriptor, context);

        // Listen extension will check that method is valid
        check(descriptor.extDescriptors.get(ListenParamExtension.KEY) != null,
                "Required @%s parameter of type %s or %s not defined", Listen.class.getSimpleName(),
                OLiveResultListener.class.getSimpleName(), LiveQueryListener.class.getSimpleName());
        return descriptor;
    }

    @Override
    protected OCommandRequest createQueryCommand(final CommandMethodDescriptor descriptor,
                                                 final SqlCommandDescriptor desc) {
        // live listener will be applied by @Listen extension
        return new OLiveQuery<Object>(desc.command, null);
    }
}
