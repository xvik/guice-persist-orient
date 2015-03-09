package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to group multiply index definitions.
 * <p>Don't forget to assign different names, because default names will be the same.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(MultipleIndexesFiledExtension.class)
public @interface Indexes {

    /**
     * @return index definitions
     */
    Index[] value();
}
