package ru.vyarus.guice.persist.orient.repository.delegate;

import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException;

/**
 * Delegate method execution exception.
 * Used to describe delegate execution context.
 *
 * @author Vyacheslav Rusakov
 * @since 28.02.2015
 */
public class DelegateMethodException extends MethodExecutionException {

    public DelegateMethodException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
