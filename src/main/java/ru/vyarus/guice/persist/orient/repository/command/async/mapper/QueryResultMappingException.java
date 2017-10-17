package ru.vyarus.guice.persist.orient.repository.command.async.mapper;

import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException;

/**
 * Thrown to indicate result mapping problem for {@link QueryListener}.
 *
 * @author Vyacheslav Rusakov
 * @since 15.10.2017
 */
public class QueryResultMappingException extends MethodExecutionException {

    public QueryResultMappingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
