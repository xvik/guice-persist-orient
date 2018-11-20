package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;

import java.lang.annotation.Annotation;

/**
 * Scheme model type extension, activated by annotation, annotated with {@link SchemeTypeInit}.
 * Use to extend default orient model registration behaviour.
 * <p>
 * Use {@link ru.vyarus.guice.persist.orient.db.util.Order} to order extensions.
 * <p>
 * Extension is obtained from guice context. If prototype scope used, different instances will be used for each
 * model (but same instance will be used for before/after calls for processing class).
 *
 * @param <A> annotation type
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
public interface TypeExtension<A extends Annotation> {

    /**
     * Called before type registration with orient.
     *
     * @param db         database object
     * @param descriptor model type descriptor object
     * @param annotation extension specific annotation
     */
    void beforeRegistration(ODatabaseObject db, SchemeDescriptor descriptor, A annotation);

    /**
     * Called after type registration with orient.
     *
     * @param db         database object
     * @param descriptor model type descriptor object
     * @param annotation extension specific annotation
     */
    void afterRegistration(ODatabaseObject db, SchemeDescriptor descriptor, A annotation);
}
