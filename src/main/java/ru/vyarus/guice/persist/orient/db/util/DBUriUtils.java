package ru.vyarus.guice.persist.orient.db.util;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.core.db.OrientDB;

/**
 * Database uri utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 23.11.2018
 */
public final class DBUriUtils {

    public static final String REMOTE = "remote:";
    public static final String MEMORY = "memory:";

    private DBUriUtils() {
    }

    /**
     * Orient db requires now separate base url and database name but it's simpler
     * to configure using single uri composing both parts. For example:
     * <ul>
     * <li>memory:dbname</li>
     * <li>plocal:/opt/databases/dbname</li>
     * <li>remote:localhost/dbname</li>
     * </ul>
     * This method splits unified declaration into two parts useful for orient api.
     *
     * @param uri full database uri (including file system path or remote host and database name)
     * @return array of two elements: base url (for {@link OrientDB} object) and database name.
     */
    public static String[] parseUri(final String uri) {
        final String[] res;
        if (uri.startsWith(MEMORY)) {
            res = new String[]{MEMORY, uri.substring(MEMORY.length())};
        } else {
            final int idx = uri.lastIndexOf('/');
            Preconditions.checkState(idx > 0, "Invalid database uri: %s", uri);
            res = new String[]{uri.substring(0, idx), uri.substring(idx + 1)};
        }
        return res;
    }

    /**
     * @param uri database uri
     * @return true if uri targets remote database
     */
    public static boolean isRemote(final String uri) {
        return uri.startsWith(REMOTE);
    }

    /**
     * @param uri database uri
     * @return true if uri targets memory database
     */
    public static boolean isMemory(final String uri) {
        return uri.startsWith(MEMORY);
    }
}
