package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.FIELD
import static java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(FieldExtension)
@interface FieldExt {

}