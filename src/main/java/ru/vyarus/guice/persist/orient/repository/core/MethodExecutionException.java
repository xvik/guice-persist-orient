package ru.vyarus.guice.persist.orient.repository.core;

/**
 * Thrown to indicate repository method execution exception (orient execution error).
 *
 * @author Vyacheslav Rusakov
 * @since 22.09.2014
 */
public class MethodExecutionException extends RuntimeException {

    public MethodExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
