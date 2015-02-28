package ru.vyarus.guice.persist.orient.repository.core.result.converter;

import ru.vyarus.guice.persist.orient.repository.RepositoryException;

/**
 * Thrown to indicate problem during result conversion.
 *
 * @author Vyacheslav Rusakov
 * @since 28.08.2014
 */
public class ResultConversionException extends RepositoryException {

    public ResultConversionException(final String message) {
        super(message);
    }

    public ResultConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
