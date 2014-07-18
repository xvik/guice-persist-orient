package ru.vyarus.guice.persist.orient.model.autoscan;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.model.ModelInitializer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;

/**
 * <p>Initialize model from classpath entities annotated with {@code ru.vyarus.guice.persist.orient.model.autoscan.Persistent}
 * annotation.</p>
 * <p>Requires "orient.model.package" guice constant, for package name where to scan entities (to reduce scanning scope)</p>
 * <p>Useful for package by feature approach when many packages could contain model classes</p>
 * <p>Note: Package is not important for orient, so classes may move between runs. But pay attention to class names to avoid collisions.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@Singleton
public class AutoscanModelInitializer implements ModelInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoscanModelInitializer.class);

    private String appPkg;

    @Inject
    public AutoscanModelInitializer(@Named("orient.model.package") String appPkg) {
        this.appPkg = appPkg;
    }

    @Override
    public void initialize(OObjectDatabaseTx db) {
        // auto create schema for new classes
        db.setAutomaticSchemaGeneration(true);

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forPackage(appPkg))
                        .setScanners(new TypeAnnotationsScanner())
        );
        // register all beans annotated with @Persistent (to allow storing model in separate packages)
        // note that orient ignore entity package, so its safe to move entity between packages
        Set<Class<?>> model = reflections.getTypesAnnotatedWith(Persistent.class);
        Preconditions.checkState(model.size() > 0,
                "No model classes found in classpath with base package '" + appPkg + "'");

        for (Class<?> modelClazz : model) {
            LOGGER.info("Orient model class found: {}", modelClazz.getName());
            db.getEntityManager().registerEntityClass(modelClazz);
        }
    }
}
