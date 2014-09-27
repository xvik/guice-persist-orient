package ru.vyarus.guice.persist.orient.finder.internal;

/**
 * Thrown to indicate incorrect finder definition.
 *
 * @author Vyacheslav Rusakov
 * @since 28.08.2014
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class FinderDefinitionException extends RuntimeException {

    public FinderDefinitionException(final String message) {
        super(message);
    }

    public FinderDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Shortcut to check and throw definition exception.
     *
     * @param condition condition to validate
     * @param message fail message
     * @param args fail message arguments
     */
    public static void check(final boolean condition, final String message, final Object... args) {
        if (!condition) {
            throw new FinderDefinitionException(String.format(message, args));
        }
    }
}
