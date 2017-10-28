package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.drop;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model type extension to drop indexes. If index not exist, nothing will happen.
 * <p>
 * May be used to re-create index (together with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.Index} or
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.CompositeIndex} annotations).
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(DropIndexesTypeExtension.class)
public @interface DropIndexes {

    /**
     * @return index names to drop
     */
    String[] value();
}
