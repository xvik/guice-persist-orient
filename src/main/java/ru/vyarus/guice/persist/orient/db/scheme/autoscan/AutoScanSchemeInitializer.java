package ru.vyarus.guice.persist.orient.db.scheme.autoscan;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.AbstractObjectInitializer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;

/**
 * <p>Initialize model from classpath entities annotated with {@code ru.vyarus.guice.persist.orient.db.scheme.autoscan.Persistent}
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
                                     final Provider<OObjectDatabaseTx> dbProvider) {
        super(dbProvider);
        this.appPkg = appPkg;
    }

    @Override
    public void init(final OObjectDatabaseTx db) {
        logger.info("Initializing database scheme by searching annotated classes in package: {}", appPkg);
        // auto create schema for new classes
        db.setAutomaticSchemaGeneration(true);

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forPackage(appPkg))
                        .setScanners(new TypeAnnotationsScanner())
        );
        // register all beans annotated with @Persistent (to allow storing model in separate packages)
        // note that orient ignore entity package, so its safe to move entity between packages
        final Set<Class<?>> model = reflections.getTypesAnnotatedWith(Persistent.class);
        Preconditions.checkState(model.size() > 0,
                "No model classes found in classpath with base package '" + appPkg + "'");

        for (Class<?> modelClazz : model) {
            logger.info("Registering model class: {}", modelClazz.getName());
            db.getEntityManager().registerEntityClass(modelClazz);
        }
    }
}
