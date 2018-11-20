package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.bad

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension

import java.lang.annotation.Annotation

/**
 * @author Vyacheslav Rusakov 
 * @since 02.07.2015
 */
class BadTypeExtImpl implements TypeExtension<BadTypeExt> {

    BadTypeExtImpl() {
        throw new UnsupportedOperationException()
    }

    @Override
    void beforeRegistration(ODatabaseObject db, SchemeDescriptor descriptor, BadTypeExt annotation) {

    }

    @Override
    void afterRegistration(ODatabaseObject db, SchemeDescriptor descriptor, BadTypeExt annotation) {

    }
}
