package ru.vyarus.guice.persist.orient.db.scheme.impl;

import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default model initializer.
 * Enables automatic schema creation and registers all classes in package provided with "orient.model.package" constant.
 * Constant may contain multiple packages, separated with comma.
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@Singleton
public class PackageSchemeInitializer extends AbstractObjectInitializer {

    @Inject
    public PackageSchemeInitializer(@Named("orient.model.package") final String modelPkgs,
                                    final Provider<ODatabaseObject> dbProvider,
                                    final ObjectSchemeInitializer schemeInitializer) {
        super(dbProvider, schemeInitializer, Matchers.any(), modelPkgs.split(","));
    }
}
