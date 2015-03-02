package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Support for jdk8 and guava Optional objects.
 * Have to use reflection for jdk optional creation (in order not to limit compilation for jdk8 only),
 * so guava optional should be a bit faster.
 *
 * @author Vyacheslav Rusakov
 * @since 27.09.2014
 */
public final class Optionals {
    private static final List<String> OPTIONAL_TYPES = ImmutableList.of(
            "com.google.common.base.Optional",
            "java.util.Optional");
    private static Method jdk8OptionalFactory;

    private Optionals() {
    }

    /**
     * @param type type
     * @return true if type is optional
     */
    public static boolean isOptional(final Class<?> type) {
        return OPTIONAL_TYPES.contains(type.getName());
    }

    /**
     * @param object value object
     * @param target target type (java8 or guava Optional)
     * @return optional instance
     */
    public static Object create(final Object object, final Class<?> target) {
        return com.google.common.base.Optional.class.equals(target)
                ? com.google.common.base.Optional.fromNullable(object) : jdk8(target, object);
    }

    /**
     * Only this will cause optional class loading and fail for earlier jdk.
     *
     * @param object object for conversion
     * @return optional instance
     */
    private static Object jdk8(final Class<?> type, final Object object) {
        try {
            // a bit faster than resolving it each time
            if (jdk8OptionalFactory == null) {
                lookupOptionalFactoryMethod(type);
            }
            return jdk8OptionalFactory.invoke(null, object);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate jdk Optional", e);
        }
    }

    private static void lookupOptionalFactoryMethod(final Class<?> type)
            throws NoSuchMethodException {
        synchronized (Optionals.class) {
            if (jdk8OptionalFactory == null) {
                jdk8OptionalFactory = type.getMethod("ofNullable", Object.class);
            }
        }
    }
}
