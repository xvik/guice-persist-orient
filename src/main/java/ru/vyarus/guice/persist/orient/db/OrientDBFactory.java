package ru.vyarus.guice.persist.orient.db;

import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import ru.vyarus.guice.persist.orient.db.util.DBUriUtils;

/**
 * Object used to store user configuration performed in {@link ru.vyarus.guice.persist.orient.OrientModule} and
 * {@link OrientDB} instance creation. Available for injection as guice bean.
 * <p>
 * May be used for configured info consultation.
 * <p>
 * Tests may indirectly enable remote database automatic creation using shortcut method
 * {@link #enableAutoCreationRemoteDatabase(String, String, ODatabaseType)} (but note that this will not override
 * values directly specified in
 * {@link ru.vyarus.guice.persist.orient.OrientModule#autoCreateRemoteDatabase(String, String, ODatabaseType)}).
 *
 * @author Vyacheslav Rusakov
 * @since 21.11.2018
 */
public class OrientDBFactory {

    public static final String REMOTE_USER = "orient.remote.user";
    public static final String REMOTE_PASSWORD = "orient.remote.password";
    public static final String REMOTE_TYPE = "orient.remote.type";

    private final String uri;
    private final String dbUrl;
    private final String dbName;
    private final String user;
    private final String password;
    private final boolean autoCreateLocal;
    private final OrientDBConfig config;

    private String serverUser;
    private String serverPassword;
    private ODatabaseType dbType;

    @SuppressWarnings("checkstyle:ParameterNumber")
    public OrientDBFactory(final String uri,
                           final String user,
                           final String password,
                           final boolean autoCreateLocal,
                           final OrientDBConfig config,
                           final String serverUser,
                           final String serverPassword,
                           final ODatabaseType dbType) {
        this.uri = uri;
        this.user = user;
        this.autoCreateLocal = autoCreateLocal;
        this.password = password;
        this.config = config;
        this.serverUser = serverUser;
        this.serverPassword = serverPassword;
        this.dbType = dbType;

        final String[] parse = DBUriUtils.parseUri(uri);
        this.dbUrl = parse[0];
        this.dbName = parse[1];
    }

    /**
     * @return full database uri (combines location part and database name)
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return location part of configured database uri
     * (without database name; used for {@link OrientDB} construction)
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * @return database name (from configured uri)
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @return configured database user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return configured database password
     */
    public String getPassword() {
        return password;
    }

    /**
     * For remote database db type must be explicitly configured in
     * {@link ru.vyarus.guice.persist.orient.OrientModule#autoCreateRemoteDatabase(String, String, ODatabaseType)},
     * otherwise method will return null (!).
     * <p>
     * Note: when indirect remote credentials used (
     * {@link #enableAutoCreationRemoteDatabase(String, String, ODatabaseType)}) get type may still return null,
     * because credentials are applied just before instance creation.
     * <p>
     * For local database types, type is detected by connection uri.
     *
     * @return database type or null in case of remote connection, when auto creation not enabled
     */
    public ODatabaseType getDbType() {
        return isRemote() ? dbType
                : isMemory() ? ODatabaseType.MEMORY : ODatabaseType.PLOCAL;
    }

    /**
     * Configured with {@link ru.vyarus.guice.persist.orient.OrientModule#autoCreateLocalDatabase(boolean)},
     * and {@link ru.vyarus.guice.persist.orient.OrientModule#autoCreateRemoteDatabase(String, String, ODatabaseType)}.
     * <p>
     * Note tha local database auto creation is enabled by default and remote is not (because it required additional
     * user credentials).
     *
     * @return true if database must be created automatically, false otherwise
     */
    public boolean isAutoCreate() {
        return (isRemote() && isAutoCreateRemote()) || (!isRemote() && autoCreateLocal);
    }

    /**
     * @return true if configured database is remote database, false otherwise
     */
    public boolean isRemote() {
        return DBUriUtils.isRemote(uri);
    }

    /**
     * NOTE: it will always return false for remote connection, even if memory storage used for database on remote
     * server. Option is intended to detect direct in-memory database only (mostly used in tests).
     *
     * @return true if configured database is in-memory (local) database, false otherwise
     */
    public boolean isMemory() {
        return DBUriUtils.isMemory(uri);
    }

    /**
     * Method intended for internal usage by {@link ru.vyarus.guice.persist.orient.db.DatabaseManager}: it creates
     * {@link OrientDB} instance (on lifecycle start), available for injection with provider
     * ({@code Provider<OrientDB>}) and used by all pools and other apis in order to create connections to
     * configured database.
     * <p>
     * There is not much sense to use it directly, otherwise then using it in tests to delete remote database
     * after persistence lifecycle stop:
     * <pre>{@code
     *      OrientDB db = factory.createOrientDB()
     *      if (db.exists(factory.getDbName())) {
     *          db.drop(factory.getDbName())
     *      }
     *      db.close()
     * }</pre>
     *
     * @return created {@link OrientDB} instance (always new)
     * @see #enableAutoCreationRemoteDatabase(String, String, ODatabaseType)
     */
    public OrientDB createOrientDB() {
        // perform lookup just before instance creation to simplify configuration (otherwise it might
        // be complicated to execute configuration before orient module creation)
        lookupRemoteConfig();

        return isAutoCreateRemote()
                ? new OrientDB(getDbUrl(), serverUser, serverPassword, config)
                : new OrientDB(getDbUrl(), config);
    }


    /**
     * May be used in tests to auto-create remote database without direct usage of
     * {@link ru.vyarus.guice.persist.orient.OrientModule#autoCreateRemoteDatabase(String, String, ODatabaseType)}
     * (without changes in code).
     * <p>
     * Configuration is stored using system properties. It must be called before persistence lifecycle startup.
     * NOTE: indirect configuration does not override manual configuration performed directly in module (with
     * {@link ru.vyarus.guice.persist.orient.OrientModule#autoCreateRemoteDatabase(String, String, ODatabaseType)})!
     * <p>
     * System properties could be cleared using {@link #disableAutoCreationRemoteDatabase()}. It is assumed
     * that cleanup method will be called on test cleanup.
     *
     * @param serverUser     remote server user
     * @param serverPassword remote server password
     * @param type           remote servre database type (plocal or memory)
     */
    public static void enableAutoCreationRemoteDatabase(final String serverUser,
                                                        final String serverPassword,
                                                        final ODatabaseType type) {
        System.setProperty(REMOTE_USER, serverUser);
        System.setProperty(REMOTE_PASSWORD, serverPassword);
        System.setProperty(REMOTE_TYPE, type.name());
    }

    /**
     * Clears system properties set by {@link #enableAutoCreationRemoteDatabase(String, String, ODatabaseType)}.
     * Assumed to be used in tests.
     */
    public static void disableAutoCreationRemoteDatabase() {
        System.clearProperty(REMOTE_USER);
        System.clearProperty(REMOTE_PASSWORD);
        System.clearProperty(REMOTE_TYPE);
    }

    private boolean isAutoCreateRemote() {
        return serverUser != null;
    }

    private void lookupRemoteConfig() {
        if (serverUser == null) {
            // tests support
            serverUser = System.getProperty(REMOTE_USER);
            serverPassword = System.getProperty(REMOTE_PASSWORD);
            final String type = System.getProperty(REMOTE_TYPE);
            dbType = type == null ? null : ODatabaseType.valueOf(type);
        }
    }
}
