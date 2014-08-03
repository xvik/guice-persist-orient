package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.inject.Provider;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

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
    public PackageSchemeInitializer(final @Named("orient.model.package") String modelPkg,
                                    final Provider<OObjectDatabaseTx> dbProvider) {
        super(dbProvider);
        this.modelPkg = modelPkg;
    }

    @Override
    public void init(final OObjectDatabaseTx db) {
        logger.info("Initializing database scheme from classes in package: {}", modelPkg);
        // auto create schema for new classes
        db.setAutomaticSchemaGeneration(true);
        // register all classes in package (native orient feature)
        db.getEntityManager().registerEntityClasses(modelPkg);
    }
}
