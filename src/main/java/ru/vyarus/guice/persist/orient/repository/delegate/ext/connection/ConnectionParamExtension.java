package ru.vyarus.guice.persist.orient.repository.delegate.ext.connection;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.delegate.param.DelegateParamsContext;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateExtension;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor;

import javax.inject.Singleton;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * {@link ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.Connection} parameter annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
@Singleton
public class ConnectionParamExtension implements MethodParamExtension<DelegateMethodDescriptor,
        DelegateParamsContext, Connection>, DelegateExtension<DelegateMethodDescriptor> {

    private static final String KEY = ConnectionParamExtension.class.getName();

    @Override
    public void processParameters(final DelegateMethodDescriptor descriptor,
                                  final DelegateParamsContext context,
                                  final List<ParamInfo<Connection>> paramsInfo) {
        check(paramsInfo.size() == 1, "Duplicate @%s parameter", Connection.class.getSimpleName());
        final ParamInfo<Connection> paramInfo = paramsInfo.get(0);
        descriptor.extDescriptors.put(KEY, paramInfo.position);
    }

    @Override
    public void amendParameters(final DelegateMethodDescriptor descriptor, final Object[] targetArgs,
                                final Object instance, final Object... arguments) {
        final Integer position = (Integer) descriptor.extDescriptors.get(KEY);
        final Class<?> methodConnType = descriptor.method.getParameterTypes()[position];
        final Object connection = descriptor.executor.getConnection();
        checkExec(methodConnType.isAssignableFrom(connection.getClass()),
                "Bad connection type declared %s, when actual is %s",
                methodConnType.getSimpleName(), connection.getClass().getSimpleName());
        targetArgs[position] = connection;
    }
}
