package ru.vyarus.guice.persist.orient.repository.command.core;

import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException;

/**
 * Command method extensions execution exception.
 * Used to describe command execution context.
 *
 * @author Vyacheslav Rusakov
 * @since 28.02.2015
 */
public class CommandMethodException extends MethodExecutionException {

    public CommandMethodException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
