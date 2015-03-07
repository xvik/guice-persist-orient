package ru.vyarus.guice.persist.orient.db.scheme;

import ru.vyarus.guice.persist.orient.db.PersistException;

/**
 * Thrown to indicate scheme initialization error.
 *
 * @author Vyacheslav Rusakov
 * @since 29.08.2014
 */
public class SchemeInitializationException extends PersistException {

    public SchemeInitializationException(final String message) {
        super(message);
    }

    public SchemeInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Shortcut to check and throw scheme exception.
     *
     * @param condition condition to validate
     * @param message   fail message
     * @param args      fail message arguments
     */
    public static void check(final boolean condition, final String message, final Object... args) {
        if (!condition) {
            throw new SchemeInitializationException(String.format(message, args));
        }
    }
}
