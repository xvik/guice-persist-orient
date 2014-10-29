package ru.vyarus.guice.persist.orient.finder.internal.query;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.persist.finder.Finder;
import ru.vyarus.guice.persist.orient.finder.internal.query.pagination.PaginationAnalyzer;
import ru.vyarus.guice.persist.orient.finder.internal.query.params.ParamsAnalyzer;
import ru.vyarus.guice.persist.orient.finder.internal.query.placeholder.PlaceholderAnalyzer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;

/**
 * Analyze query finder method: validate definition correctness and build descriptor object.
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
public final class FinderQueryDescriptorFactory {

    private FinderQueryDescriptorFactory() {
    }

    public static FinderQueryDescriptor buildDescriptor(final Method method, final Map<String, Type> generics) {
        final Finder finderAnnotation = method.getAnnotation(Finder.class);

        final String functionName = Strings.emptyToNull(finderAnnotation.namedQuery());
        final String query = Strings.emptyToNull(finderAnnotation.query());
        check(Strings.isNullOrEmpty(functionName) || Strings.isNullOrEmpty(query),
                "Choose what to use function or query, but not both");

        final FinderQueryDescriptor descriptor = new FinderQueryDescriptor();
        descriptor.isFunctionCall = functionName != null;
        descriptor.query = descriptor.isFunctionCall ? functionName : query;

        descriptor.placeholders = PlaceholderAnalyzer.analyzePlaceholders(method, generics, descriptor.query);
        analyzeParameters(method, descriptor);
        return descriptor;
    }

    private static void analyzeParameters(final Method method, final FinderQueryDescriptor descriptor) {
        final List<Integer> skip = Lists.newArrayList();
        // recognize pagination annotations
        descriptor.pagination = PaginationAnalyzer.analyzePaginationParameters(method);
        if (descriptor.pagination != null) {
            skip.addAll(descriptor.pagination.getBoundIndexes());
        }
        // recognize placeholder annotations
        PlaceholderAnalyzer.analyzePlaceholderParameters(method, descriptor.placeholders,
                descriptor.query, skip);
        if (descriptor.placeholders != null) {
            skip.addAll(descriptor.placeholders.getBoundIndexes());
        }
        // map all remaining parameters
        descriptor.params = ParamsAnalyzer.analyzeParameters(method, skip);
    }
}
