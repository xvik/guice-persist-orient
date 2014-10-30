package ru.vyarus.guice.persist.orient.finder.internal.delegate.method;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Method filters.
 *
 * @author Vyacheslav Rusakov
 * @since 30.10.2014
 */
public final class MethodFilters {

    private MethodFilters() {
    }

    /**
     * @param possibilities methods to reduce
     * @param method        method name (may be null)
     * @return all methods with specified name or original collection
     */
    public static Collection<MethodDescriptor> filterByMethodName(final Collection<MethodDescriptor> possibilities,
                                                                  final String method) {
        return method == null ? possibilities : Collections2.filter(possibilities,
                new Predicate<MethodDescriptor>() {
                    @Override
                    public boolean apply(@Nonnull final MethodDescriptor input) {
                        return input.method.getName().equals(method);
                    }
                });
    }

    /**
     * Looks for most specific type for each parameter.
     *
     * @param possibilities methods to reduce
     * @param paramsCount   finder method params count
     * @return possibly reduced collection or original one
     */
    public static Collection<MethodDescriptor> filterByClosestParams(
            final Collection<MethodDescriptor> possibilities, final int paramsCount) {
        Collection<MethodDescriptor> res = null;
        for (int i = 0; i < paramsCount; i++) {
            final Collection<MethodDescriptor> filtered = filterByParam(possibilities, i);
            if (res != null) {
                if (filtered.size() < res.size()) {
                    res = filtered;
                }
            } else {
                res = filtered;
            }
        }
        return res;
    }

    @SuppressWarnings("PMD.UselessParentheses")
    private static Collection<MethodDescriptor> filterByParam(final Collection<MethodDescriptor> possibilities,
                                                              final int pos) {
        final List<MethodDescriptor> filtered = Lists.newArrayList();
        Class<?> previous = null;
        for (MethodDescriptor desc : possibilities) {
            final Class<?> targetType = desc.method.getParameterTypes()[getParameterPosition(desc, pos)];
            if (previous == null
                    || (previous.isAssignableFrom(targetType) && !previous.equals(targetType))) {
                // more specific type found
                previous = targetType;
                filtered.clear();
                filtered.add(desc);
            } else if (previous.equals(targetType)) {
                filtered.add(desc);
            }
        }
        return filtered;
    }

    private static int getParameterPosition(final MethodDescriptor desc, final int pos) {
        // target method argument position
        int targetPos = 0;
        // finder method reference position
        int actualParamCounter = 0;
        while (actualParamCounter != pos) {
            if (!desc.isExtended() || !desc.extendedParamsPositions.contains(targetPos)) {
                actualParamCounter++;
            }
            targetPos++;
        }
        return targetPos;
    }
}
