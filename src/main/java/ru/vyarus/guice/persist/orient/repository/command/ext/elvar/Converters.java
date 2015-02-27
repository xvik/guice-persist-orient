package ru.vyarus.guice.persist.orient.repository.command.ext.elvar;

import com.google.common.base.Strings;

/**
 * El var converters.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
public final class Converters {

    /**
     * Default value to string converter.
     * Uses object toString method.
     * If value is null, null returned.
     */
    public static final ValueConverter<Object> DEFAULT = new ValueConverter<Object>() {
        @Override
        public String convert(final Object value) {
            return value == null ? null : Strings.emptyToNull(value.toString());
        }
    };

    /**
     * Class value converter.
     * Returns class name or null if null provided.
     */
    public static final ValueConverter<Class> CLASS = new ValueConverter<Class>() {
        @Override
        public String convert(final Class value) {
            return value == null ? "" : value.getSimpleName();
        }
    };

    private Converters() {
    }

    /**
     * Converter used to convert type to string.
     *
     * @param <T> value type
     */
    public interface ValueConverter<T> {

        /**
         * @param value value object
         * @return string representation
         */
        String convert(T value);
    }
}
