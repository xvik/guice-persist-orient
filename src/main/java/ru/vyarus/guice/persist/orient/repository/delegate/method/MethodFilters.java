package ru.vyarus.guice.persist.orient.repository.delegate.method;

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
    public static Collection<MatchedMethod> filterByMethodName(final Collection<MatchedMethod> possibilities,
                                                               final String method) {
        return method == null ? possibilities : Collections2.filter(possibilities,
                new Predicate<MatchedMethod>() {
                    @Override
                    public boolean apply(@Nonnull final MatchedMethod input) {
                        return input.method.getName().equals(method);
                    }
                });
    }

    /**
     * Looks for most specific type for each parameter.
     *
     * @param possibilities methods to reduce
     * @param paramsCount   repository method params count
     * @return possibly reduced collection or original one
     */
    public static Collection<MatchedMethod> filterByClosestParams(
            final Collection<MatchedMethod> possibilities, final int paramsCount) {
        Collection<MatchedMethod> res = null;
        for (int i = 0; i < paramsCount; i++) {
            final Collection<MatchedMethod> filtered = filterByParam(possibilities, i);
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

    private static Collection<MatchedMethod> filterByParam(final Collection<MatchedMethod> possibilities,
                                                           final int pos) {
        final List<MatchedMethod> filtered = Lists.newArrayList();
        Class<?> previous = null;
        for (MatchedMethod desc : possibilities) {
            final Class<?> targetType = desc.paramInfos.get(pos).type;
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

    /**
     * @param possibilities methods to reduce
     * @return extended method or null if no extended or more than one extended methods found
     */
    public static MatchedMethod findSingleExtended(final Collection<MatchedMethod> possibilities) {
        final Collection<MatchedMethod> extended = Collections2.filter(possibilities,
                new Predicate<MatchedMethod>() {
                    @Override
                    public boolean apply(@Nonnull final MatchedMethod input) {
                        return input.extended;
                    }
                });
        return extended.size() == 1 ? extended.iterator().next() : null;
    }
}
