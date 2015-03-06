package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension

import java.lang.annotation.Annotation
import java.lang.reflect.Field

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
abstract class AbstractFieldExt<A extends Annotation> implements FieldExtension<A> {

    boolean before
    boolean after

    @Override
    void beforeRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, Field field, A annotation) {
        before = true
    }

    @Override
    void afterRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, Field field, A annotation) {
        after = true
    }
}
