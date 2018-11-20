package ru.vyarus.guice.persist.orient.db.scheme.impl;

import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Initialize model from classpath entities annotated with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent} annotation.
 * <p>
 * Requires "orient.model.package" guice constant (defined in module), for package name where
 * to scan entities (to reduce scanning scope). Constant may contain multiple packages, separated with comma.
 * <p>
 * Useful for package by feature approach when many packages could contain model classes.
 * <p>
 * Note: Package is not important for orient, so classes may move between runs.
 * But pay attention to class names to avoid collisions.
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@Singleton
public class AutoScanSchemeInitializer extends AbstractObjectInitializer {

    @Inject
    public AutoScanSchemeInitializer(@Named("orient.model.package") final String appPkgs,
                                     final Provider<ODatabaseObject> dbProvider,
                                     final ObjectSchemeInitializer schemeInitializer) {
        super(dbProvider, schemeInitializer, Matchers.annotatedWith(Persistent.class), appPkgs.split(","));
    }
}
