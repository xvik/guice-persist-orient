package ru.vyarus.guice.persist.orient.db.user;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * User manager holds context user credentials. Using different user may be important
 * for scheme initialization (when user with higher rights may be required) or to
 * rely on <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/Security.html">orient security model</a>.
 * <p>By default, configured user and password returned. To use specific user use
 * {@code executeWithUser} method. Specific user binds to current thread. Most likely,
 * user changing logic will be implemented as filter to handle request processing as specific user.</p>
 * <p>Note: user can't be changed during ongoing transaction and if specific user already defined.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 04.11.2014
 */
@Singleton
public class UserManager {
    private final Logger logger = LoggerFactory.getLogger(UserManager.class);

    private final TransactionManager transactionManager;
    private final UserCredentials defaultUser;
    private final ThreadLocal<UserCredentials> specificUser = new ThreadLocal<UserCredentials>();

    @Inject
    public UserManager(final TransactionManager transactionManager,
                       @Named("orient.user") final String user,
                       @Named("orient.password") final String password) {
        this.transactionManager = transactionManager;
        this.defaultUser = create(user, password);
    }

    /**
     * @return current user name (specific or default)
     */
    public String getUser() {
        return getCurrentUser().user;
    }

    /**
     * @return current user password (specific or default)
     */
    public String getPassword() {
        return getCurrentUser().password;
    }

    /**
     * Action approach is important to explicitly define scope of specific user and
     * properly cleanup state (which may be not done in case of direct override).
     *
     * @param user       specific user name
     * @param password   specific user password
     * @param userAction logic to execute with specific user
     * @param <T>        type of returned result (may be Void)
     * @return action result (may be null)
     * @throws Throwable if error happens it will not be caught here
     */
    public <T> T executeWithUser(final String user, final String password,
                                 final SpecificUserAction<T> userAction) throws Throwable {
        Preconditions.checkState(!transactionManager.isTransactionActive(),
                "User can't be changed during transaction");
        Preconditions.checkState(specificUser.get() == null,
                "Specific user already defined as '%s'",
                specificUser.get() != null ? specificUser.get().user : null);
        specificUser.set(create(user, password));
        logger.trace("Use specific user: {}", user);
        try {
            return userAction.execute();
        } finally {
            specificUser.remove();
        }
    }

    private UserCredentials create(final String user, final String password) {
        Preconditions.checkNotNull(user, "Database user name required");
        Preconditions.checkNotNull(password, "Database user password required");
        return new UserCredentials(user, password);
    }

    private UserCredentials getCurrentUser() {
        return Objects.firstNonNull(specificUser.get(), defaultUser);
    }

    /**
     * Internal credentials holder.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    private static final class UserCredentials {
        public final String user;
        public final String password;

        UserCredentials(final String user, final String password) {
            this.user = user;
            this.password = password;
        }
    }
}
