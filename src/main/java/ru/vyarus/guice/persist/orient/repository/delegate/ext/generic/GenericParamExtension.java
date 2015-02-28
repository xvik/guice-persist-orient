package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic;

import com.google.common.collect.Lists;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.delegate.param.DelegateParamsContext;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateExtension;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * @author Vyacheslav Rusakov
 * @since 08.02.2015
 */
@Singleton
public class GenericParamExtension implements MethodParamExtension<DelegateMethodDescriptor,
        DelegateParamsContext, Generic>, DelegateExtension<DelegateMethodDescriptor> {

    private static final String KEY = GenericParamExtension.class.getName();

    @Override
    public void processParameters(final DelegateMethodDescriptor descriptor, final DelegateParamsContext context,
                                  final List<ParamInfo<Generic>> paramsInfo) {
        final List<ParamInfo> res = Lists.newArrayList();
        for (ParamInfo<Generic> paramInfo : paramsInfo) {
            res.add(createParam(paramInfo, context));
        }
        descriptor.extDescriptors.put(KEY, res);
    }

    @Override
    public void amendParameters(final DelegateMethodDescriptor descriptor, final Object[] targetArgs,
                                final Object instance, final Object... arguments) {
        @SuppressWarnings("unchecked")
        final List<ParamInfo> generics = (List<ParamInfo>) descriptor.extDescriptors.get(KEY);
        for (ParamInfo generic : generics) {
            targetArgs[generic.position] = generic.type;
        }
    }

    @SuppressWarnings("unchecked")
    private ParamInfo createParam(final ParamInfo<Generic> paramInfo, final DelegateParamsContext context) {
        final Class<?> requiredType = paramInfo.annotation.genericHolder();
        final GenericsContext generics = requiredType == Object.class
                ? context.getCallerContext().generics : context.getCallerContext().generics.type(requiredType);
        final String generic = paramInfo.annotation.value();
        final int position = paramInfo.position;
        final Method method = context.getDescriptorContext().method;
        check(method.getParameterTypes()[position].equals(Class.class),
                "Generic type parameter must be Class, but it's %s", paramInfo.type.getSimpleName());

        final Class<?> type = generics.generic(generic);
        check(type != null, "Generic type name '%s' not found in %s", generic, generics.currentClass());
        return new ParamInfo(paramInfo.position, type);
    }
}
