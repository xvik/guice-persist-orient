package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Scheme model field extension, activated by annotation, annotated with {@link SchemeFieldInit}.
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
public interface FieldExtension<A extends Annotation> {

    /**
     * Called before type registration with orient.
     *
     * @param db         database object
     * @param descriptor model type descriptor object
     * @param field      source extension field
     * @param annotation extension specific annotation
     */
    void beforeRegistration(ODatabaseObject db, SchemeDescriptor descriptor, Field field, A annotation);

    /**
     * Called after type registration with orient.
     *
     * @param db         database object
     * @param descriptor model type descriptor object
     * @param field      source extension field
     * @param annotation extension specific annotation
     */
    void afterRegistration(ODatabaseObject db, SchemeDescriptor descriptor, Field field, A annotation);
}
