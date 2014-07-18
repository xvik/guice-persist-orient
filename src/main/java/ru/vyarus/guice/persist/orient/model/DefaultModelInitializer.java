package ru.vyarus.guice.persist.orient.model;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default model initializer.
 * Enables automatic schema creation and registers all classes in package provided with "orient.model.package" constant.
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@Singleton
public class DefaultModelInitializer implements ModelInitializer {


    private String modelPkg;

    @Inject
    public DefaultModelInitializer(@Named("orient.model.package") String modelPkg) {
        this.modelPkg = modelPkg;
    }

    @Override
    public void initialize(OObjectDatabaseTx db) {
        // auto create schema for new classes
        db.setAutomaticSchemaGeneration(true);
        // register all classes in package
        db.getEntityManager().registerEntityClasses(modelPkg);
    }
}
