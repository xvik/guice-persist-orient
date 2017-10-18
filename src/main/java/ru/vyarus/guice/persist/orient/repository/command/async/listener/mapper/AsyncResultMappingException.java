package ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper;

import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException;

/**
 * Thrown to indicate result mapping problem for {@link AsyncQueryListener}.
 *
 * @author Vyacheslav Rusakov
 * @since 15.10.2017
 */
public class AsyncResultMappingException extends MethodExecutionException {

    public AsyncResultMappingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
