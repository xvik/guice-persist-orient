package ru.vyarus.guice.persist.orient.repository.core;

import ru.vyarus.guice.persist.orient.repository.RepositoryException;

/**
 * Thrown to indicate repository method execution exception (orient execution error).
 *
 * @author Vyacheslav Rusakov
 * @since 22.09.2014
 */
public class MethodExecutionException extends RepositoryException {

    public MethodExecutionException(final String message) {
        super(message);
    }

    public MethodExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Shortcut to check and throw execution exception.
     *
     * @param condition condition to validate
     * @param message   fail message
     * @param args      fail message arguments
     */
    public static void checkExec(final boolean condition, final String message, final Object... args) {
        if (!condition) {
            throw new MethodExecutionException(String.format(message, args));
        }
    }
}
