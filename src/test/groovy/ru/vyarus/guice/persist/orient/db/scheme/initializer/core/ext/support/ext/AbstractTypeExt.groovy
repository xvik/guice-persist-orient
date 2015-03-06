package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension

import java.lang.annotation.Annotation

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
abstract class AbstractTypeExt<A extends Annotation> implements TypeExtension<A> {

    boolean before
    boolean after


    @Override
    void beforeRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, A annotation) {
        before = true
    }

    @Override
    void afterRegistration(OObjectDatabaseTx db, SchemeDescriptor descriptor, A annotation) {
        after = true
    }
}
