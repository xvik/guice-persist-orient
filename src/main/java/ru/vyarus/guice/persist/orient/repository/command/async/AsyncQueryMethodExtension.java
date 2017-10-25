package ru.vyarus.guice.persist.orient.repository.command.async;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery;
import com.orientechnologies.orient.core.sql.query.OSQLNonBlockingQuery;
import ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener;
import ru.vyarus.guice.persist.orient.repository.command.core.AbstractCommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.ListenParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Future;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * {@link AsyncQuery} method extension.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@Singleton
public class AsyncQueryMethodExtension extends AbstractCommandExtension<CommandMethodDescriptor, AsyncQuery> {

    /**
     * Extension key used to store query "blocking" setting.
     */
    public static final String EXT_BLOCKING = "ASYNC_QUERY_BLOCKING";

    @Inject
    public AsyncQueryMethodExtension(final SpiService spiService) {
        super(spiService);
    }

    @Override
    public CommandMethodDescriptor createDescriptor(final DescriptorContext context, final AsyncQuery annotation) {
        final CommandMethodDescriptor descriptor = new CommandMethodDescriptor();
        descriptor.command = Strings.emptyToNull(annotation.value());
        final boolean blocking = annotation.blocking();
        descriptor.extDescriptors.put(EXT_BLOCKING, blocking);
        checkReturnType(context.method.getReturnType(), blocking);

        analyzeElVars(descriptor, context);
        analyzeParameters(descriptor, context);

        check(descriptor.extDescriptors.get(ListenParamExtension.KEY) != null,
                "Required @%s parameter of type %s or %s not defined", Listen.class.getSimpleName(),
                OCommandResultListener.class.getSimpleName(), AsyncQueryListener.class.getSimpleName());
        return descriptor;
    }

    @Override
    protected OCommandRequest createQueryCommand(final CommandMethodDescriptor descriptor,
                                                 final SqlCommandDescriptor desc) {
        final boolean blocking = (Boolean) descriptor.extDescriptors.get(EXT_BLOCKING);
        // correct listener will be set by @Listen extension
        if (blocking) {
            return new OSQLAsynchQuery(desc.command);
        } else {
            return new OSQLNonBlockingQuery(desc.command, null);
        }
    }

    private void checkReturnType(final Class<?> returnType, final boolean blocking) {
        if (blocking) {
            check(void.class.equals(returnType) || Void.class.equals(returnType),
                    "Async query method must be void, because no results returned from query");
        } else {
            check(void.class.equals(returnType) || Void.class.equals(returnType)
                            || Future.class.equals(returnType),
                    "Non blocking async query method may be only void or return %s because "
                            + "no results returned from query", Future.class.getSimpleName());
        }
    }
}
