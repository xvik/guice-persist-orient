package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;

import java.util.List;

/**
 * Scheme initialization utils.
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
public final class SchemeUtils {

    private SchemeUtils() {
    }

    /**
     * Resolves model class hierarchy (types recognized by orient).
     *
     * @param type model class
     * @return class hierarchy
     */
    public static List<Class<?>> resolveHierarchy(final Class<?> type) {
        final List<Class<?>> res = Lists.newArrayList();
        Class<?> current = type;
        while (!Object.class.equals(current) && current != null) {
            res.add(current);
            current = current.getSuperclass();
        }
        return res;
    }

    /**
     * Assigns base class in scheme for provided root type (for example, to make class vertex type
     * it must extend V).
     *
     * @param db     database object
     * @param root   root model class (the lowest class in model hierarchy)
     * @param target target super class to assign
     * @param logger caller specific logger
     */
    public static void assignSuperclass(final OObjectDatabaseTx db, final String root, final String target,
                                        final Logger logger) {
        if (db.getMetadata().getSchema().existsClass(root)) {
            final OClass schemeRoot = db.getMetadata().getSchema().getClass(root).getSuperClass();

            Preconditions.checkState(schemeRoot == null || target.equals(schemeRoot.getName()),
                    "Model class %s can't be registered as extending %s, because its "
                            + "already extends different class %s",
                    root, target, schemeRoot == null ? null : schemeRoot.getName());
            if (schemeRoot == null) {
                logger.debug("Assigning superclass {} to {}", target, root);
                command(db, "alter class %s superclass %s", root, target);
            }
            return;
        }
        logger.debug("Creating model class scheme {} as extension to {}", root, target);
        command(db, "create class %s extends %s", root, target);
    }

    /**
     * Calls schema change sql command.
     *
     * @param db      database object
     * @param command command with string format placeholders
     * @param args    string format placeholders args (important: not query args!)
     */
    public static void command(final OObjectDatabaseTx db, final String command, final Object... args) {
        db.command(new OCommandSQL(String.format(command, args))).execute();
    }

    /**
     * Drops named index. Safe to call even if index not exist.
     *
     * @param db        database object
     * @param indexName index name
     * @see com.orientechnologies.orient.core.index.OIndexManagerProxy#dropIndex(java.lang.String)
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public static void dropIndex(final OObjectDatabaseTx db, final String indexName) {
        // Separated to overcome findbugs false positive "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT" for dropIndex method.
        db.getMetadata().getIndexManager().dropIndex(indexName);
    }
}
