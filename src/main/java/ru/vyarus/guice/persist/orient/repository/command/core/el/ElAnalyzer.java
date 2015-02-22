package ru.vyarus.guice.persist.orient.repository.command.core.el;

import ru.vyarus.java.generics.resolver.context.GenericsContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Query could be parametrized with variables. By default only generic descriptors supported:
 * method's own class generic names could be used as variables, which will be resolved as class names (without
 * package).
 * Extensions could manage other variable values. They only have to register owned variables in descriptor
 * (otherwise validation will fail).
 *
 * @author Vyacheslav Rusakov
 * @since 15.02.2015
 */
public final class ElAnalyzer {

    private ElAnalyzer() {
    }

    /**
     * Analyze query string for variables.
     *
     * @param genericsContext generics context (set to the method owner type)
     * @param query           query string
     * @return descriptor object if variables found, null otherwise
     */
    public static ElDescriptor analyzeQuery(final GenericsContext genericsContext, final String query) {
        final List<String> vars = ElUtils.findVars(query);
        ElDescriptor descriptor = null;
        if (!vars.isEmpty()) {
            descriptor = new ElDescriptor(vars);
            checkGenericVars(descriptor, genericsContext);
        }
        return descriptor;
    }

    private static void checkGenericVars(final ElDescriptor descriptor, final GenericsContext generics) {
        if (generics != null) {
            for (Map.Entry<String, Type> entry : generics.genericsMap().entrySet()) {
                final String key = entry.getKey();
                if (descriptor.vars.contains(key)) {
                    // using just class name, because orient don't need package
                    descriptor.directValues.put(key, generics.resolveClass(entry.getValue()).getSimpleName());
                }
            }
        }
    }
}
