package ru.vyarus.guice.persist.orient.db.user;

/**
 * Action performed with specific user (different from default one).
 * Must be passed into
 * {@link ru.vyarus.guice.persist.orient.db.user.UserManager#executeWithUser(String, String, SpecificUserAction)}.
 * <p>Specific user must be set before transaction opening. By implementing this interface and calling
 * user change method will not start any implicit transaction.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 04.11.2014
 */
public interface SpecificUserAction {

    /**
     *
     * @param <T> return type (may be Void)
     * @return return value (or null)
     * @throws Throwable any error thrown during execution
     */
    <T> T execute() throws Throwable;
}
