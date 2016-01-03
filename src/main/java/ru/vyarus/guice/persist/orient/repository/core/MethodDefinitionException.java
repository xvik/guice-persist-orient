package ru.vyarus.guice.persist.orient.repository.core;

import ru.vyarus.guice.persist.orient.repository.RepositoryException;

/**
 * Thrown to indicate incorrect repository method definition.
 *
 * @author Vyacheslav Rusakov
 * @since 28.08.2014
 */
public class MethodDefinitionException extends RepositoryException {

    public MethodDefinitionException(final String message) {
        super(message);
    }

    public MethodDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Shortcut to check and throw definition exception.
     *
     * @param condition condition to validate
     * @param message   fail message
     * @param args      fail message arguments
     */
    public static void check(final boolean condition, final String message, final Object... args) {
        if (!condition) {
            throw new MethodDefinitionException(String.format(message, args));
        }
    }
}
