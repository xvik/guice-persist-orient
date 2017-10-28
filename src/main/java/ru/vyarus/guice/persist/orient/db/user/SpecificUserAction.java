package ru.vyarus.guice.persist.orient.db.user;

/**
 * Action performed with specific user (different from default one).
 * <p>
 * When used to set user outside of transaction
 * {@link ru.vyarus.guice.persist.orient.db.user.UserManager#executeWithUser(String, String, SpecificUserAction)},
 * affects connection user for all transactions created inside callback (used when multiply connections
 * must be affected).
 * <p>
 * When used to set user inside transaction
 * {@link ru.vyarus.guice.persist.orient.db.user.UserManager#executeWithTxUser(String, SpecificUserAction)},
 * affects only current connection user. This allows to use user specific logic like security ro current user
 * assigning to record.
 *
 * @param <T> return type (may be Void)
 * @author Vyacheslav Rusakov
 * @since 04.11.2014
 */
public interface SpecificUserAction<T> {

    /**
     * @return return value (or null)
     * @throws Throwable any error thrown during execution
     */
    T execute() throws Throwable;
}
