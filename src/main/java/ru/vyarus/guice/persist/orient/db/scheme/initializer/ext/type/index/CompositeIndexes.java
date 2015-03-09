package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model type extension to group multiply composite index definitions.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(MultipleIndexesTypeExtension.class)
public @interface CompositeIndexes {

    /**
     * @return index definitions
     */
    CompositeIndex[] value();
}
