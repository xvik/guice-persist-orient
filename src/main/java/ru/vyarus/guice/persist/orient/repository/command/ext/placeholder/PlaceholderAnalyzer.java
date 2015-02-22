package ru.vyarus.guice.persist.orient.repository.command.ext.placeholder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.repository.command.core.el.ElDescriptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Placeholder parameters analysis support for query extensions.
 * <p>Enum and String parameters supported as placeholders.</p>
 * <p>String placeholders should have possible values declarations for security, otherwise warning showed.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class PlaceholderAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceholderAnalyzer.class);

    private PlaceholderAnalyzer() {
    }

    public static PlaceholderDescriptor analyzeDeclarations(final Method method, final ElDescriptor el) {

        final PlaceholderDescriptor descriptor = new PlaceholderDescriptor();
        final PlaceholderValues valueAnnotation = method.getAnnotation(PlaceholderValues.class);
        if (valueAnnotation != null) {
            registerPlaceholderValues(valueAnnotation, el.vars, descriptor);
        }
        final Placeholders valuesAnnotation = method.getAnnotation(Placeholders.class);
        if (valuesAnnotation != null) {
            for (PlaceholderValues values : valuesAnnotation.value()) {
                registerPlaceholderValues(values, el.vars, descriptor);
            }
        }
        return descriptor;
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

    public static void bindPlaceholder(final String name, final int position,
                                       final PlaceholderDescriptor context, final Method method,
                                       final Collection<String> placeholderDefaults) {
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
                                + "or define values with @PlaceholderValues annotation in repository "
                                + "method {}#{}. Without explicit check your query "
                                + "is vulnerable for injection.",
                        name, method.getDeclaringClass(), method.getName());
            }
        }
        context.parametersIndex.put(name, position);
    }
}
