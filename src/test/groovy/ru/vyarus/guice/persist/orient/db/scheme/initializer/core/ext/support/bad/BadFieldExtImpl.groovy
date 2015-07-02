package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.bad

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension

import java.lang.annotation.Annotation
import java.lang.reflect.Field

/**
 * @author Vyacheslav Rusakov 
 * @since 02.07.2015
 */
class BadFieldExtImpl implements FieldExtension<BadFieldExt> {

    BadFieldExtImpl() {
        throw new UnsupportedOperationException()
    }

    @Override
    void beforeRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, Field field, BadFieldExt annotation) {

    }

    @Override
    void afterRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, Field field, BadFieldExt annotation) {

    }
}
