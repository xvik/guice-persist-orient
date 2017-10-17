package ru.vyarus.guice.persist.orient.repository.command.ext.listen;

import com.google.inject.Injector;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandRequestAbstract;
import ru.vyarus.guice.persist.orient.db.util.Order;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.live.LiveListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.query.QueryListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.support.ListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.support.RequiresRecordConversion;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.java.generics.resolver.GenericsResolver;
import ru.vyarus.java.generics.resolver.context.MethodGenericsContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;
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

    private final Injector injector;

    private final ListenerTypeSupport queryHandler = new QueryListenerTypeSupport();
    private final ListenerTypeSupport liveHandler = new LiveListenerTypeSupport();

    @Inject
    public ListenParamExtension(final Injector injector) {
        this.injector = injector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<Listen>> paramsInfo) {
        check(paramsInfo.size() == 1, "Duplicate @%s definition", Listen.class.getSimpleName());
        final ParamInfo<Listen> param = paramsInfo.get(0);
        final Method method = context.getDescriptorContext().method;
        final Class<?> returnType = method.getReturnType();

        final String query = descriptor.command.toLowerCase();
        final ListenParamDescriptor extDesc = new ListenParamDescriptor(selectHandler(query),
                param.position,
                resolveListenerGeneric(context.getDescriptorContext().generics.method(method),
                        param.type, param.position),
                param.annotation.transactional());

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

        ((OCommandRequestAbstract) query).setResultListener(extDesc.handler
                .processListener(query, listener, injector, extDesc.transactional,
                        resolveTargetType(listener.getClass(), extDesc.generic)));
    }

    private ListenerTypeSupport selectHandler(final String query) {
        return query.startsWith("live") ? liveHandler : queryHandler;
    }

    /**
     * Resolve generic specified directly in listener parameter (e.g. {@code QueryListener<Model>}). It would be
     * useful if, for some reason, correct type could not be resolved from listener instance (in most cases it
     * would be possible).
     *
     * @param generics     repository method generics context
     * @param listenerType type of specified listener
     * @param position     listener parameter position
     * @return resolved generic or Object
     */
    private Class<?> resolveListenerGeneric(final MethodGenericsContext generics, final Class listenerType,
                                            final int position) {
        // TO BE IMPROVED here must be correct resolution of RequiresRecordConversion generic, but it requires
        // some improvements in generics-resolver
        // In most cases even this simplified implementation will work
        if (RequiresRecordConversion.class.isAssignableFrom(listenerType)
                && listenerType.getTypeParameters().length > 0) {
            try {
                // questionable assumption that the first generic is a target type, but will work in most cases
                return generics.resolveGenericOf(generics.currentMethod().getGenericParameterTypes()[position]);
            } catch (Exception ex) {
                // never happen
                throw new IllegalStateException("Parameter generic resolution failed", ex);
            }
        }
        return Object.class;
    }

    /**
     * Resolve target conversion type either from listener instance or, if its not possible, from
     * listener parameter declaration.
     *
     * @param listenerType       listener instance type
     * @param declaredTargetType listener generic declared in method declaration
     * @return target conversion type or null if conversion is not required
     */
    private Class<?> resolveTargetType(final Class<?> listenerType, final Class<?> declaredTargetType) {
        Class<?> target = null;
        if (RequiresRecordConversion.class.isAssignableFrom(listenerType)) {
            target = GenericsResolver.resolve(listenerType)
                    .type(RequiresRecordConversion.class).generic("T");

            // if generic could not be resolved from listener instance, use method parameter declaration (last resort)
            if (Object.class.equals(target)) {
                target = declaredTargetType;
            }
        }
        return target;
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

        /**
         * Indicates that listener must be wrapped with transaction.
         */
        public final boolean transactional;

        /**
         * Custom listeners like {@link ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener}
         * and {@link com.orientechnologies.orient.core.query.live.OLiveQueryListener} may be generified. This generic
         * could be used in cases when actual (maybe more concrete) generic can't be resolved from the listener
         * instance (last resort).
         */
        public final Class<?> generic;

        ListenParamDescriptor(final ListenerTypeSupport handler,
                              final int position,
                              final Class<?> generic,
                              final boolean transactional) {
            this.handler = handler;
            this.position = position;
            this.generic = generic;
            this.transactional = transactional;
        }
    }
}
