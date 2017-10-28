package ru.vyarus.guice.persist.orient.db.scheme.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for persistent entities.
 * Register {@link ru.vyarus.guice.persist.orient.db.scheme.impl.AutoScanSchemeInitializer} in guice context
 * or use {@link ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule} and all
 * annotated classes will be registered automatically.
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Persistent {
}
