package ru.vyarus.guice.persist.orient.repository.core.util;

import ru.vyarus.guice.ext.core.generator.DynamicClassGenerator;

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
}
