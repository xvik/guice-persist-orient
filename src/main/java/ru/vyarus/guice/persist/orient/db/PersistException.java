package ru.vyarus.guice.persist.orient.db;

/**
 * Root exception class for library specific exceptions.
 *
 * @author Vyacheslav Rusakov
 * @since 28.02.2015
 */
public class PersistException extends RuntimeException {

    public PersistException(final String message) {
        super(message);
    }

    public PersistException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
