package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.recreate;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model type extension to re-create model scheme (drop current scheme and perform fresh registration).
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(RecreateTypeExtension.class)
public @interface Recreate {
}
