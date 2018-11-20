package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.bad

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension

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
    void beforeRegistration(ODatabaseObject db, SchemeDescriptor descriptor, Field field, BadFieldExt annotation) {

    }

    @Override
    void afterRegistration(ODatabaseObject db, SchemeDescriptor descriptor, Field field, BadFieldExt annotation) {

    }
}
