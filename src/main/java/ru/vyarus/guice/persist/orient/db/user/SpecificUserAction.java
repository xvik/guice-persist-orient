package ru.vyarus.guice.persist.orient.db.user;

/**
 * Action performed with specific user (different from default one).
 * Must be passed into
 * {@link ru.vyarus.guice.persist.orient.db.user.UserManager#executeWithUser(String, String, SpecificUserAction)}.
 * <p>Specific user must be set before transaction opening. By implementing this interface and calling
 * user change method will not start any implicit transaction.</p>
 *
 * @param <T> return type (may be Void)
 * @author Vyacheslav Rusakov
 * @since 04.11.2014
 */
public interface SpecificUserAction<T> {

    /**
     *
     * @return return value (or null)
     * @throws Throwable any error thrown during execution
     */
    T execute() throws Throwable;
}
