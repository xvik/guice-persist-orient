package ru.vyarus.guice.persist.orient.repository.command.ext.pagination;

import com.google.common.collect.ImmutableList;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import java.lang.annotation.Annotation;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Base class for {@link ru.vyarus.guice.persist.orient.repository.command.ext.pagination.Skip} and
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.pagination.Limit} pagination extensions.
 *
 * @param <A> annotation type
 * @author Vyacheslav Rusakov
 * @since 07.02.2015
 */
public abstract class AbstractPaginationExtension<A extends Annotation>
        implements MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, A> {

    private static final List<Class> PRIMITIVE_NUMBERS = ImmutableList.<Class>of(int.class, long.class);

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<A>> paramsInfo) {
        final ParamInfo<A> paramInfo = paramsInfo.get(0);
        final String type = paramInfo.annotation.annotationType().getSimpleName();

        check(paramsInfo.size() == 1, "Duplicate @%s definition", type);
        isNumber(paramInfo.type, String.format("Number must be used as @%s parameter", type));
        descriptor.extDescriptors.put(getKey(), paramInfo.position);
    }

    protected abstract String getKey();

    protected Number getValue(final CommandMethodDescriptor descriptor, final Object... arguments) {
        final Integer position = (Integer) descriptor.extDescriptors.get(getKey());
        return (Number) (position != null ? arguments[position] : null);
    }

    private void isNumber(final Class type, final String message) {
        final boolean isPrimitiveNumber = type.isPrimitive() && PRIMITIVE_NUMBERS.contains(type);
        check(isPrimitiveNumber || Number.class.isAssignableFrom(type), message);
    }
}
