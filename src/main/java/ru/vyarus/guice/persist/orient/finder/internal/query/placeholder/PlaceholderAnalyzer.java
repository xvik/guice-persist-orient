package ru.vyarus.guice.persist.orient.finder.internal.query.placeholder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException;
import ru.vyarus.guice.persist.orient.finder.internal.query.params.ParamsUtils;
import ru.vyarus.guice.persist.orient.finder.placeholder.Placeholder;
import ru.vyarus.guice.persist.orient.finder.placeholder.PlaceholderValues;
import ru.vyarus.guice.persist.orient.finder.placeholder.Placeholders;
import ru.vyarus.guice.persist.orient.finder.placeholder.StringTemplateUtils;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;

/**
 * Placeholders analysis support for finder methods.
 * Interface generics also may be used as placeholder. In this case placeholder value will be set
 * automatically.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class PlaceholderAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceholderAnalyzer.class);

    private PlaceholderAnalyzer() {
    }

    public static PlaceholderDescriptor analyzePlaceholders(final Method method,
                                                            final GenericsContext generics,
                                                            final String query) {
        final List<String> placeholders = getPlaceholders(query);

        PlaceholderDescriptor descriptor = null;
        if (!placeholders.isEmpty()) {
            descriptor = new PlaceholderDescriptor();
            descriptor.values = HashMultimap.create();
            final PlaceholderValues valueAnnotation = method.getAnnotation(PlaceholderValues.class);
            if (valueAnnotation != null) {
                registerPlaceholderValues(valueAnnotation, placeholders, descriptor);
            }
            final Placeholders valuesAnnotation = method.getAnnotation(Placeholders.class);
            if (valuesAnnotation != null) {
                for (PlaceholderValues values : valuesAnnotation.value()) {
                    registerPlaceholderValues(values, placeholders, descriptor);
                }
            }
            checkGenericPlaceholders(placeholders, descriptor, generics);
        }
        return descriptor;
    }

    public static void analyzePlaceholderParameters(final Method method,
                                                    final PlaceholderDescriptor descriptor,
                                                    final String query, final List<Integer> skip) {
        ParamsUtils.process(method, new PlaceholderParamVisitor(descriptor, method), skip);
        validatePlaceholdersParamsDefinition(descriptor, query);
    }

    private static List<String> getPlaceholders(final String query) {
        List<String> placeholders;
        try {
            placeholders = StringTemplateUtils.findPlaceholders(query);
        } catch (IllegalStateException ex) {
            throw new FinderDefinitionException(ex.getMessage(), ex);
        }
        return placeholders;
    }

    private static void checkGenericPlaceholders(final List<String> placeholders,
                                                 final PlaceholderDescriptor descriptor,
                                                 final GenericsContext generics) {
        if (generics != null) {
            for (Map.Entry<String, Type> entry : generics.genericsMap().entrySet()) {
                final String key = entry.getKey();
                if (placeholders.contains(key)) {
                    if (descriptor.genericParameters == null) {
                        descriptor.genericParameters = Maps.newHashMap();
                    }
                    // using just class name, because orient don't need package
                    descriptor.genericParameters.put(key, generics.resolveClass(entry.getValue()).getSimpleName());
                }
            }
        }
    }

    private static void registerPlaceholderValues(final PlaceholderValues values, final List<String> placeholders,
                                                  final PlaceholderDescriptor descriptor) {
        final String name = values.name();
        check(!descriptor.values.containsKey(name),
                "Duplicate placeholder '%s' values definition", name);
        check(placeholders.contains(name),
                "Default values defined for placeholder '%s' not used in query", name);
        descriptor.values.putAll(name, Arrays.asList(values.values()));
    }

    private static void bindPlaceholder(final String name, final int position,
                                        final PlaceholderDescriptor context, final Method method,
                                        final Collection<String> placeholderDefaults) {
        if (context.parametersIndex == null) {
            context.parametersIndex = Maps.newHashMap();
        }
        check(!context.parametersIndex.containsKey(name),
                "Duplicate placeholder parameter %s declaration at position %s", name, position);
        final Class paramType = method.getParameterTypes()[position];
        final boolean isEnum = paramType.isEnum();
        check(String.class.equals(paramType) || isEnum,
                "Unsupported placeholder '%s' type at position %s. Only string end enum could be used",
                name, position);
        if (isEnum) {
            check(placeholderDefaults.isEmpty(), "Placeholder param '%s' at position %s is enum. "
                    + "Explicit defaults definition is not required", name, position);
        } else {
            if (placeholderDefaults.isEmpty()) {
                LOGGER.warn("No default values registered for placeholder parameter {}. Either use enum "
                                + "or define values with @PlaceholderValues annotation in finder "
                                + "method {}#{}. Without explicit check your query "
                                + "is vulnerable for injection.",
                        name, method.getDeclaringClass(), method.getName());
            }
        }
        context.parametersIndex.put(name, position);
    }

    private static void validatePlaceholdersParamsDefinition(
            final PlaceholderDescriptor descriptor, final String query) {
        if (descriptor != null) {
            try {
                final List<String> empty = Collections.emptyList();
                // count usual and generic parameters
                final List<String> params = ImmutableList.<String>builder()
                        .addAll(descriptor.parametersIndex != null
                                ? descriptor.parametersIndex.keySet()
                                : empty)
                        .addAll(descriptor.genericParameters != null
                                ? descriptor.genericParameters.keySet()
                                : empty).build();
                StringTemplateUtils.validate(query, params);
            } catch (IllegalStateException ex) {
                check(false, ex.getMessage());
            }
        }
    }

    /**
     * Placeholder parameters visitor. Searches for parameters annotated with @Placeholder.
     */
    private static class PlaceholderParamVisitor implements ParamsUtils.ParamVisitor {
        private final PlaceholderDescriptor descriptor;
        private final Method method;

        public PlaceholderParamVisitor(final PlaceholderDescriptor descriptor, final Method method) {
            this.descriptor = descriptor;
            this.method = method;
        }

        @Override
        public boolean accept(final Annotation annotation, final int position, final Class<?> type) {
            boolean res = false;
            if (annotation != null) {
                final Class<? extends Annotation> annotationType = annotation.annotationType();
                if (Placeholder.class.equals(annotationType)) {
                    check(descriptor != null, "Placeholder parameter used while query did "
                            + "not contain placeholders");
                    final String placeholderName = ((Placeholder) annotation).value();
                    bindPlaceholder(placeholderName, position, descriptor, method,
                            descriptor.values.get(placeholderName));
                    res = true;
                }
            }
            return res;
        }
    }
}
