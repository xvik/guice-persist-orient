package ru.vyarus.guice.persist.orient.repository.command.ext.listen;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandRequestAbstract;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.core.util.Order;

import javax.inject.Singleton;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

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

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<Listen>> paramsInfo) {
        check(paramsInfo.size() == 1, "Duplicate @%s definition", Listen.class.getSimpleName());
        check(descriptor.command.startsWith("select"), "Listener could be applied only for select queries");
        final Class<?> returnType = context.getDescriptorContext().method.getReturnType();
        check(void.class.equals(returnType) || Void.class.equals(returnType),
                "Method with listener must be void, because no results returned from query "
                        + "when listener used");
        final ParamInfo<Listen> param = paramsInfo.get(0);
        check(OCommandResultListener.class.isAssignableFrom(param.type), "Only %s can be used as result listener",
                OCommandResultListener.class.getName());
        descriptor.extDescriptors.put(KEY, param.position);
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        // not needed
    }

    @Override
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        check(query instanceof OCommandRequestAbstract,
                "@%s can't be applied to query, because command object %s doesn't support it",
                Listen.class.getSimpleName(), query.getClass().getName());
        final Integer position = (Integer) descriptor.extDescriptors.get(KEY);
        final OCommandResultListener listener = (OCommandResultListener) arguments[position];
        // null listener makes no sense: method is void and results are not handled anywhere
        Preconditions.checkNotNull(listener, "Listener can't be null");
        ((OCommandRequestAbstract) query).setResultListener(listener);
        // return type is void and as an optimization limit applied in query extension..
        // which is wrong in this particular case
        if (query.getLimit() == 1) {
            query.setLimit(-1);
        }
    }
}
