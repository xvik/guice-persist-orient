package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.scheme.annotation.EdgeType;
import ru.vyarus.guice.persist.orient.db.scheme.annotation.VertexType;

/**
 * <p>Base class for object mapping initializers (jpa-like approach).</p>
 * Object initialization specific:
 * <ul>
 * <li>Orient ignore package, so class may be moved between packages</li>
 * <li>When entity field removed, orient will hold all data already stored in records of that type</li>
 * <li>When entity field type changes, it WILL NOT be migrated automatically.</li>
 * <li>When class renamed orient will register it as new entity and you will have to manually migrate old table
 * (or use sql commands to rename entity in db scheme)</li>
 * </ul>
 * <p>If model class annotated with @EdgeType or @VertexType annotation, then graph compatible schema will be created
 * (e.g. "create class Model extends V" for vertex type). It will be possible to create edges and vertexes of
 * such type later through graph api)</p>
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public abstract class AbstractObjectInitializer implements SchemeInitializer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Provider<OObjectDatabaseTx> dbProvider;
    // provider to avoid circular dependency
    private Provider<DatabaseManager> databaseManager;

    protected AbstractObjectInitializer(Provider<OObjectDatabaseTx> dbProvider, Provider<DatabaseManager> databaseManager) {
        this.dbProvider = dbProvider;
        this.databaseManager = databaseManager;
    }

    @Override
    public void initialize() {
        init(dbProvider.get());
    }

    /**
     * Called to init schema with predefined object connection.
     *
     * @param db object connection
     */
    protected abstract void init(OObjectDatabaseTx db);

    /**
     * If class annotated with @Edge or @Vertex will create
     *
     * @param modelClass model class to map to scheme
     */
    @SuppressWarnings("unchecked")
    protected void registerClass(final Class modelClass) {
        logger.info("Registering model class: {}", modelClass);
        final VertexType vertex = (VertexType) modelClass.getAnnotation(VertexType.class);
        final EdgeType edge = (EdgeType) modelClass.getAnnotation(EdgeType.class);
        Preconditions.checkState(vertex == null || edge == null, "You can't use both Vertex and Edge annotations together, choose one.");

        if (vertex != null || edge != null) {
            registerEntityAsGraph(modelClass, vertex != null);
        }

        final OObjectDatabaseTx db = dbProvider.get();
        db.getEntityManager().registerEntityClass(modelClass);
    }

    private void registerEntityAsGraph(Class modelClass, boolean isVertex) {
        if (!databaseManager.get().isTypeSupported(DbType.GRAPH)) {
            logger.warn("Entity {} graph declaration ignored, because no graph database support available.",
                    modelClass);
            return;
        }
        // hierarchy support (topmost class must be vertex)
        Class<?> supertype = modelClass;
        Class<?> baseType = modelClass;
        while (!Object.class.equals(supertype = supertype.getSuperclass()) && supertype != null) {
            baseType = supertype;
        }


        final String targetSuper = isVertex ? "V" : "E";
        final OObjectDatabaseTx db = dbProvider.get();
        if (db.getMetadata().getSchema().existsClass(baseType.getSimpleName())) {
            OClass cls = db.getMetadata().getSchema().getClass(baseType).getSuperClass();
            Preconditions.checkState(cls != null,
                    String.format("Model class %s can't be registered as extending %s, because %s already registered and can't be updated " +
                                    "to support graph according to annotation",
                            modelClass, targetSuper, baseType));

            Preconditions.checkState(cls.getName().equals(targetSuper),
                    String.format("Model class %s can't be registered as extending %s, because %s already registered " +
                                    "with different superclass %s and can't be " +
                                    "updated to support graph according to annotation",
                            modelClass, targetSuper, baseType, cls.getName()));

            // nothing to do - class already registered correctly
            return;
        }
        logger.debug("Creating model class scheme {} as extension to {}", baseType, targetSuper);
        db.command(new OCommandSQL("create class " + baseType.getSimpleName() + " extends " + targetSuper)).execute();
    }
}
