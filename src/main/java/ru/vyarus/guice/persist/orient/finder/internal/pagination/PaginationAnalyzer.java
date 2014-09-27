package ru.vyarus.guice.persist.orient.finder.internal.pagination;

import com.google.common.collect.ImmutableList;
import com.google.inject.persist.finder.FirstResult;
import com.google.inject.persist.finder.MaxResults;
import ru.vyarus.guice.persist.orient.finder.internal.params.ParamsUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;

/**
 * Analyze pagination finder arguments.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class PaginationAnalyzer {

    private static final List<Class> PRIMITIVE_NUMBERS = ImmutableList.<Class>of(int.class, long.class);

    private PaginationAnalyzer() {
    }

    /**
     * Search for pagination parameters.
     *
     * @param method method to analyze
     * @return pagination description or null if no pagination parameters found
     */
    @SuppressWarnings("PMD.NullAssignment")
    public static PaginationDescriptor analyzePaginationParameters(final Method method) {
        final PaginationDescriptor descriptor = new PaginationDescriptor();
        ParamsUtil.process(method, new PaginationParamVisitor(descriptor), null);
        return descriptor.isEmpty() ? null : descriptor;
    }

    private static void isNumber(final Class type, final String message) {
        final boolean isPrimitiveNumber = type.isPrimitive() && PRIMITIVE_NUMBERS.contains(type);
        check(isPrimitiveNumber || Number.class.isAssignableFrom(type), message);
    }

    /**
     * Pagination parameters visitor. Recognize parameters annotated with @FirstResult and @MaxResults.
     */
    private static class PaginationParamVisitor implements ParamsUtil.ParamVisitor {
        private final PaginationDescriptor descriptor;

        public PaginationParamVisitor(final PaginationDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public boolean accept(final Annotation annotation, final int position, final Class<?> type) {
            boolean res = false;
            if (annotation != null) {
                final Class<? extends Annotation> annotationType = annotation.annotationType();
                if (FirstResult.class.equals(annotationType)) {
                    check(descriptor.firstResultParamIndex == null, "Duplicate @FirstResult definition");
                    descriptor.firstResultParamIndex = position;
                    isNumber(type, "Number must be used as @FirstResult parameter");
                    res = true;
                } else if (MaxResults.class.equals(annotationType)) {
                    check(descriptor.maxResultsParamIndex == null, "Duplicate @MaxResults definition");
                    descriptor.maxResultsParamIndex = position;
                    isNumber(type, "Number must be used as @MaxResults parameter");
                    res = true;
                }
            }
            return res;
        }
    }
}
