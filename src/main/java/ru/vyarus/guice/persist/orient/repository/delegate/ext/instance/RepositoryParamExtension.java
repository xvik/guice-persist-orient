package ru.vyarus.guice.persist.orient.repository.delegate.ext.instance;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.delegate.param.DelegateParamsContext;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateExtension;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor;

import javax.inject.Singleton;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * {@link Repository} delegate annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 08.02.2015
 */
@Singleton
public class RepositoryParamExtension implements MethodParamExtension<DelegateMethodDescriptor,
        DelegateParamsContext, Repository>, DelegateExtension<DelegateMethodDescriptor> {

    private static final String KEY = RepositoryParamExtension.class.getName();

    @Override
    public void processParameters(final DelegateMethodDescriptor descriptor, final DelegateParamsContext context,
                                  final List<ParamInfo<Repository>> paramsInfo) {
        check(paramsInfo.size() == 1, "Duplicate repository instance parameter");
        final ParamInfo<Repository> paramInfo = paramsInfo.get(0);
        check(paramInfo.type.isAssignableFrom(context.getCallerContext().type),
                "Repository instance parameter is incompatible: found %s when required %s",
                paramInfo.type.getSimpleName(), context.getCallerContext().type.getSimpleName());
        descriptor.extDescriptors.put(KEY, paramInfo.position);
    }

    @Override
    public void amendParameters(final DelegateMethodDescriptor descriptor, final Object[] targetArgs,
                                final Object instance, final Object... arguments) {
        final int position = (Integer) descriptor.extDescriptors.get(KEY);
        targetArgs[position] = instance;
    }
}
