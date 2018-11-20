package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.sql.OCommandSQL;
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

    private static final String V = "V";
    private static final String E = "E";
    private static final String VERTEX = "vertex";
    private static final String EDGE = "edge";

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
     * Assigns base class in scheme for provided model type (for example, to make class vertex type
     * it must extend V).
     *
     * @param db        database object
     * @param modelType model class
     * @param target    target super class to assign
     * @param logger    caller specific logger
     */
    public static void assignSuperclass(final ODatabaseObject db, final Class<?> modelType, final String target,
                                        final Logger logger) {
        // searching for first existing scheme class to check hierarchy and avoid duplicates
        final OClass existing = findFirstExisting(db, modelType);
        final String modelName = modelType.getSimpleName();
        if (existing != null) {
            if (existing.isSubClassOf(target)) {
                return;
            }
            validateGraphTypes(modelName, existing, target);
        }
        if (existing != null && modelName.equals(existing.getName())) {
            logger.debug("Assigning superclass {} to {}", target, modelName);
            // adding superclass, not overriding!
            command(db, "alter class %s superclass +%s", modelName, target);
        } else {
            logger.debug("Creating model class scheme {} as extension to {}", modelName, target);
            command(db, "create class %s extends %s", modelName, target);
        }
    }

    /**
     * Calls schema change sql command.
     *
     * @param db      database object
     * @param command command with string format placeholders
     * @param args    string format placeholders args (important: not query args!)
     */
    public static void command(final ODatabaseObject db, final String command, final Object... args) {
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
    public static void dropIndex(final ODatabaseObject db, final String indexName) {
        // Separated to overcome findbugs false positive "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT" for dropIndex method.
        db.getMetadata().getIndexManager().dropIndex(indexName);
    }

    /**
     * @param db        db connection
     * @param modelType model type to examine
     * @return first class in provided model type class hierarchy which is registered in orient
     */
    private static OClass findFirstExisting(final ODatabaseObject db, final Class<?> modelType) {
        Class<?> target = null;
        if (!db.getMetadata().getSchema().existsClass(modelType.getSimpleName())) {
            final List<Class<?>> hierarchy = resolveHierarchy(modelType);
            for (Class<?> type : hierarchy) {
                if (db.getMetadata().getSchema().existsClass(type.getSimpleName())) {
                    target = type;
                }
            }
        } else {
            target = modelType;
        }
        return target == null ? null : db.getMetadata().getSchema().getClass(target);
    }

    private static void validateGraphTypes(final String modelClass, final OClass existing, final String target) {
        final boolean isVertex = V.equals(target);
        if (isVertex || E.equals(target)) {
            Preconditions.checkState(!existing.isSubClassOf(isVertex ? E : V),
                    "Model class %s can't be registered as %s type, because its already %s type",
                    modelClass, isVertex ? VERTEX : EDGE, isVertex ? EDGE : VERTEX);
        }
    }
}
