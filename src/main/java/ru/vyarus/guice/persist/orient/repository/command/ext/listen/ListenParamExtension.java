package ru.vyarus.guice.persist.orient.repository.command.ext.listen;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandRequestAbstract;
import ru.vyarus.guice.persist.orient.db.util.Order;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.type.LiveListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.type.QueryListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import javax.inject.Singleton;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * {@link Listen} parameter annotation.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@Singleton
// execute before other extensions, so they could possibly set correct limit
@Order(-10)
public class ListenParamExtension implements
        CommandExtension<CommandMethodDescriptor>,
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, Listen> {

    public static final String KEY = ListenParamExtension.class.getName();

    private final ListenerTypeSupport queryHandler = new QueryListenerTypeSupport();
    private final ListenerTypeSupport liveHandler = new LiveListenerTypeSupport();

    @Override
    @SuppressWarnings("unchecked")
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<Listen>> paramsInfo) {
        check(paramsInfo.size() == 1, "Duplicate @%s definition", Listen.class.getSimpleName());
        final ParamInfo<Listen> param = paramsInfo.get(0);
        final Class<?> returnType = context.getDescriptorContext().method.getReturnType();
        final String query = descriptor.command.toLowerCase();
        final ListenParamDescriptor extDesc = new ListenParamDescriptor(selectHandler(query), param.position);

        extDesc.handler.checkParameter(query, param, returnType);

        descriptor.extDescriptors.put(KEY, extDesc);
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        // not needed
    }

    @Override
    @SuppressWarnings("unchecked")
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        final ListenParamDescriptor extDesc = (ListenParamDescriptor) descriptor
                .extDescriptors.get(ListenParamExtension.KEY);
        final Object listener = arguments[extDesc.position];
        // null listener makes no sense: method is void and results are not handled anywhere
        checkExec(listener != null, "Listener can't be null");

        ((OCommandRequestAbstract) query).setResultListener(extDesc.handler.processListener(query, listener));
    }

    private ListenerTypeSupport selectHandler(final String query) {
        return query.startsWith("live") ? liveHandler : queryHandler;
    }

    /**
     * Extension internal model.
     *
     * @author Vyacheslav Rusakov
     * @since 29.09.2017
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public static class ListenParamDescriptor {

        /**
         * Selected listener type handler.
         */
        public final ListenerTypeSupport handler;

        /**
         * Listener parameter position.
         */
        public final int position;

        ListenParamDescriptor(final ListenerTypeSupport handler, final int position) {
            this.handler = handler;
            this.position = position;
        }
    }
}
