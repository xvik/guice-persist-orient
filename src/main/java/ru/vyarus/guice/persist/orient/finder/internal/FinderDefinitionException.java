package ru.vyarus.guice.persist.orient.finder.internal;

/**
 * Thrown to indicate incorrect finder definition.
 *
 * @author Vyacheslav Rusakov
 * @since 28.08.2014
 */
public class FinderDefinitionException extends RuntimeException {

    public FinderDefinitionException(final String message) {
        super(message);
    }

    public FinderDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
