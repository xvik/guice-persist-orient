package ru.vyarus.guice.persist.orient.finder.result;

/**
 * Thrown to indicate problem during finder result conversion.
 *
 * @author Vyacheslav Rusakov
 * @since 28.08.2014
 */
public class FinderResultConversionException extends RuntimeException {

    public FinderResultConversionException(final String message) {
        super(message);
    }

    public FinderResultConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
