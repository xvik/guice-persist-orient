package ru.vyarus.guice.persist.orient.repository.command.live.mapper;

import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException;

/**
 * Thrown to indicate result mapping problem for {@link LiveResultListener}.
 *
 * @author Vyacheslav Rusakov
 * @since 12.10.2017
 */
public class LiveResultMappingException extends MethodExecutionException {

    public LiveResultMappingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
