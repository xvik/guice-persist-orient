package ru.vyarus.guice.persist.orient.db.user;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.OrientDBFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * User manager holds context user credentials. Using different user may be important
 * for scheme initialization (when user with higher rights may be required) or to
 * rely on <a href="https://orientdb.org/docs/3.1.x/security/Security.html">orient security model</a>.
 * <p>
 * By default, configured user and password returned. To use specific user use
 * {@code executeWithUser} method. Specific user binds to current thread. Most likely,
 * user changing logic will be implemented as filter to handle request processing as specific user.
 * <p>
 * User could be changed for ongoing transaction too in order to force security checks. This change will not
 * affect connection user credentials.
 *
 * @author Vyacheslav Rusakov
 * @since 04.11.2014
 */
@Singleton
public class UserManager {
    private final Logger logger = LoggerFactory.getLogger(UserManager.class);

    private final TransactionManager transactionManager;
    private final Provider<ODatabaseDocument> connectionProvider;
    private final UserCredentials defaultUser;
    private final ThreadLocal<UserCredentials> specificUser = new ThreadLocal<>();
    private final ThreadLocal<OSecurityUser> specificTxUser = new ThreadLocal<>();

    @Inject
    public UserManager(final TransactionManager transactionManager,
                       final Provider<ODatabaseDocument> connectionProvider,
                       final OrientDBFactory dbInfo) {
        this.transactionManager = transactionManager;
        this.connectionProvider = connectionProvider;
        this.defaultUser = create(dbInfo.getUser(), dbInfo.getPassword());
    }

    /**
     * Note: in case of tx user change, connection user remain the same.
     *
     * @return current connection credentials user name (specific or default)
     */
    public String getUser() {
        return getCurrentUser().user;
    }

    /**
     * Note: in case of tx user change, connection password remain the same.
     *
     * @return current connection credentials user password (specific or default)
     */
    public String getPassword() {
        return getCurrentUser().password;
    }

    /**
     * @return true if specific user (non default) used, false for default user
     */
    public boolean isSpecificUser() {
        return specificUser.get() != null;
    }

    /**
     * Changes connection credentials user outside of transaction. Used to affect multiple transactions.
     * <p>
     * Recursive user changes are not allowed, but user change inside transaction is allowed with
     * {@link #executeWithTxUser(String, SpecificUserAction)}.
     * <p>
     * Action approach is important to explicitly define scope of specific user and
     * properly cleanup state (which may be not done in case of direct override).
     * <p>
     * Propagates runtime exceptions (orient exceptions).
     * <p>
     * IMPORTANT: connection pool will not be used for specific user! Instead explicit new connection would
     * be opened.
     *
     * @param user       specific user name
     * @param password   specific user password
     * @param userAction logic to execute with specific user
     * @param <T>        type of returned result (may be Void)
     * @return action result (may be null)
     */
    public <T> T executeWithUser(final String user, final String password,
                                 final SpecificUserAction<T> userAction) {
        Preconditions.checkState(!transactionManager.isTransactionActive(),
                "User can't be changed during transaction");
        Preconditions.checkState(specificUser.get() == null,
                "Specific user already defined as '%s'",
                specificUser.get() != null ? specificUser.get().user : null);
        specificUser.set(create(user, password));
        logger.trace("Use specific user: {}", user);
        T result = null;
        try {
            result = userAction.execute();
        } catch (Throwable th) {
            Throwables.throwIfUnchecked(th);
            throw new UserActionException(String.format("Failed to perform operation under user '%s'", user), th);
        } finally {
            specificUser.remove();
        }
        return result;
    }

    /**
     * Changes current connection user. See {@link #executeWithTxUser(
     * com.orientechnologies.orient.core.metadata.security.OSecurityUser, SpecificUserAction)}.
     * <p>
     * LIMITATION: current user must have read right on users table.
     *
     * @param user       user login
     * @param userAction logic to execute with specific user
     * @param <T>        type of returned result (may be Void)
     * @return action result (may be null)
     */
    public <T> T executeWithTxUser(final String user, final SpecificUserAction<T> userAction) {
        final boolean userChanged = checkSpecificUserConditions(user);
        final ODatabaseDocument db = connectionProvider.get();
        final T res;
        if (userChanged) {
            // this may cause security exception if current user has no access rights to users table
            final OUser specificUser = db.getMetadata().getSecurity().getUser(user);
            Preconditions.checkState(specificUser != null, "User '%s' not found", user);
            res = executeWithTxUser(specificUser, userAction);
        } else {
            res = executeWithTxUser(db.getUser(), userAction);
        }
        return res;
    }

    /**
     * Changes current connection user. Affects only current transaction and can't be used outside of transaction
     * ({@link ODatabaseDocumentInternal#setUser(com.orientechnologies.orient.core.metadata.security.OSecurityUser)}).
     * <p>
     * Recursive user changes are not allowed, so attempt to change user under already changed user will
     * lead to error. The only exception is change to the same user (in this case change is ignored).
     * <p>
     * Action approach is important to explicitly define scope of specific user and
     * properly cleanup state (which may be not done in case of direct override).
     * <p>
     * Propagates runtime exceptions (orient exceptions).
     *
     * @param user       specific user
     * @param userAction logic to execute with specific user
     * @param <T>        type of returned result (may be Void)
     * @return action result (may be null)
     */
    public <T> T executeWithTxUser(final OSecurityUser user, final SpecificUserAction<T> userAction) {
        final boolean userChanged = checkSpecificUserConditions(user.getName());
        final ODatabaseDocumentInternal db = (ODatabaseDocumentInternal) connectionProvider.get();
        final OSecurityUser original = db.getUser();
        if (userChanged) {
            // no need to track user change if user not changed
            specificTxUser.set(user);
            db.setUser(user);
        }
        T result = null;
        try {
            result = userAction.execute();
        } catch (Throwable th) {
            Throwables.throwIfUnchecked(th);
            throw new UserActionException(String.format("Failed to perform tx action with user '%s'",
                    user.getName()), th);
        } finally {
            if (userChanged) {
                db.setUser(original);
                specificTxUser.remove();
            }
        }
        return result;
    }

    private UserCredentials create(final String user, final String password) {
        Preconditions.checkNotNull(user, "Database user name required");
        Preconditions.checkNotNull(password, "Database user password required");
        return new UserCredentials(user, password);
    }

    private UserCredentials getCurrentUser() {
        return MoreObjects.firstNonNull(specificUser.get(), defaultUser);
    }

    private boolean checkSpecificUserConditions(final String login) {
        Preconditions.checkState(transactionManager.isTransactionActive(),
                "Tx user can't be changed outside of transaction");
        final ODatabaseDocument db = connectionProvider.get();
        final OSecurityUser original = db.getUser();
        final boolean userChanged = !original.getName().equals(login);
        Preconditions.checkState(specificTxUser.get() == null || !userChanged,
                "Specific user already defined for transaction as '%s'",
                specificTxUser.get() == null ? null : specificTxUser.get().getName());
        return userChanged;
    }

    /**
     * Internal credentials holder.
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    private static final class UserCredentials {
        public final String user;
        public final String password;

        UserCredentials(final String user, final String password) {
            this.user = user;
            this.password = password;
        }
    }
}
