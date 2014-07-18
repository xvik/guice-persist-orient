package ru.vyarus.guice.persist.orient.model;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@ImplementedBy(DefaultModelInitializer.class)
public interface ModelInitializer {

    void initialize(OObjectDatabaseTx db);
}
