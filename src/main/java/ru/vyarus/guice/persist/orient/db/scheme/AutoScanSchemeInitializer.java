package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.orientechnologies.common.reflection.OReflectionHelper;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;
import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * <p>Initialize model from classpath entities annotated with {@code ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent}
 * annotation.</p>
 * <p>Requires "orient.model.package" guice constant (defined in module), for package name where to scan entities (to reduce scanning scope)</p>
 * <p>Useful for package by feature approach when many packages could contain model classes</p>
 * <p>Note: Package is not important for orient, so classes may move between runs. But pay attention to class names to avoid collisions.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@Singleton
public class AutoScanSchemeInitializer extends AbstractObjectInitializer {

    private final Logger logger = LoggerFactory.getLogger(AutoScanSchemeInitializer.class);

    private String appPkg;

    @Inject
    public AutoScanSchemeInitializer(final @Named("orient.model.package") String appPkg,
                                     final Provider<OObjectDatabaseTx> dbProvider,
                                     final Provider<DatabaseManager> databaseManager) {
        super(dbProvider, databaseManager);
        this.appPkg = appPkg;
    }

    @Override
    public void init(final OObjectDatabaseTx db) {
        logger.info("Initializing database scheme by searching annotated classes in package: {}", appPkg);
        // auto create schema for new classes
        db.setAutomaticSchemaGeneration(true);


        final List<Class<?>> modelClasses = Lists.newArrayList();
        try {
            final List<Class<?>> foundClasses =
                    OReflectionHelper.getClassesFor(appPkg, Thread.currentThread().getContextClassLoader());
            for (Class<?> cls : foundClasses) {
                if (cls.isAnnotationPresent(Persistent.class)) {
                    modelClasses.add(cls);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to resolve model classes from package: " + appPkg, e);
        }
        Preconditions.checkState(modelClasses.size() > 0,
                "No model classes found in classpath with base package '" + appPkg + "'");

        for (Class<?> cls : modelClasses) {
            registerClass(cls);
        }
    }
}
