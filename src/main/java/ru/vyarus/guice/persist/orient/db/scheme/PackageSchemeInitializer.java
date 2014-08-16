package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.orientechnologies.common.reflection.OReflectionHelper;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Default model initializer.
 * Enables automatic schema creation and registers all classes in package provided with "orient.model.package" constant
 * (defined in module).
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@Singleton
public class PackageSchemeInitializer extends AbstractObjectInitializer {
    private Logger logger = LoggerFactory.getLogger(PackageSchemeInitializer.class);

    private String modelPkg;

    @Inject
    public PackageSchemeInitializer(@Named("orient.model.package") final String modelPkg,
                                    final Provider<OObjectDatabaseTx> dbProvider,
                                    final Provider<DatabaseManager> databaseManager) {
        super(dbProvider, databaseManager);
        this.modelPkg = modelPkg;
    }

    @Override
    public void init(final OObjectDatabaseTx db) {
        logger.info("Initializing database scheme from classes in package: {}", modelPkg);
        // auto create schema for new classes
        db.setAutomaticSchemaGeneration(true);

        List<Class<?>> modelClasses;
        try {
            modelClasses = OReflectionHelper.getClassesFor(modelPkg, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to resolve model classes from package: " + modelPkg, e);
        }
        Preconditions.checkState(modelClasses.size() > 0,
                "No model classes found in classpath with base package '" + modelPkg + "'");
        for (Class<?> cls : modelClasses) {
            registerClass(cls);
        }
    }
}
