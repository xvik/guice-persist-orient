package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException;

/**
 * Thrown to indicate problem during result conversion.
 *
 * @author Vyacheslav Rusakov
 * @since 28.08.2014
 */
public class ResultConversionException extends MethodExecutionException {

    public ResultConversionException(final String message) {
        super(message);
    }

    public ResultConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
