package ru.vyarus.guice.persist.orient.db.user;

import ru.vyarus.guice.persist.orient.db.PersistException;

/**
 * Exception used for wrapping checked errors during {@link ru.vyarus.guice.persist.orient.db.user.SpecificUserAction}
 * execution.
 *
 * @author Vyacheslav Rusakov
 * @since 03.03.2015
 */
public class UserActionException extends PersistException {

    public UserActionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
