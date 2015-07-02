package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.bad

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
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
    void beforeRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, BadTypeExt annotation) {

    }

    @Override
    void afterRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, BadTypeExt annotation) {

    }
}
