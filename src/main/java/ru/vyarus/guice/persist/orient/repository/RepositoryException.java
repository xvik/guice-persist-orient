package ru.vyarus.guice.persist.orient.repository;

import ru.vyarus.guice.persist.orient.db.PersistException;

/**
 * Root class for repository specific exceptions.
 *
 * @author Vyacheslav Rusakov
 * @since 28.02.2015
 */
public class RepositoryException extends PersistException {

    public RepositoryException(final String message) {
        super(message);
    }

    public RepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
