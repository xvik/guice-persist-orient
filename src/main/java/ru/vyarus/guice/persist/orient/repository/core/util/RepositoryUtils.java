package ru.vyarus.guice.persist.orient.repository.core.util;

import ru.vyarus.guice.ext.core.generator.DynamicClassGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Repository specific utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 26.10.2014
 */
public final class RepositoryUtils {

    private RepositoryUtils() {
    }

    /**
     * Resolves repository class from repository instance.
     * Method is aware of guice proxies and guice-ext-annotations dynamic class proxies (used to make interface
     * or abstract type normal class).
     *
     * @param repository repository instance
     * @return base repository type
     */
    public static Class<?> resolveRepositoryClass(final Object repository) {
        Class<?> result = repository.getClass();
        if (result.getName().contains("$$EnhancerByGuice")) {
            result = result.getSuperclass();
        }
        if (result.getName().contains(DynamicClassGenerator.DYNAMIC_CLASS_POSTFIX)) {
            result = result.getSuperclass() == Object.class ? result.getInterfaces()[0] : result.getSuperclass();
        }
        return result;
    }


    /**
     * Converts method signature to human readable string.
     *
     * @param method method to print
     * @return string representation for method
     */
    public static String methodToString(final Method method) {
        return methodToString(method.getDeclaringClass(), method);
    }

    /**
     * Converts method signature to human readable string.
     *
     * @param type   root type (method may be called not from declaring class)
     * @param method method to print
     * @return string representation for method
     */
    public static String methodToString(final Class<?> type, final Method method) {
        final StringBuilder res = new StringBuilder();
        res.append(type.getSimpleName()).append('#').append(method.getName()).append('(');
        int i = 0;
        for (Class<?> param : method.getParameterTypes()) {
            if (i > 0) {
                res.append(", ");
            }
            final Type generic = method.getGenericParameterTypes()[i];
            if (generic instanceof TypeVariable) {
                // using generic name, because its simpler to search visually in code
                res.append('<').append(((TypeVariable) generic).getName()).append('>');
            } else {
                res.append(param.getSimpleName());
            }
            i++;
        }
        res.append(')');
        return res.toString();
    }
}
