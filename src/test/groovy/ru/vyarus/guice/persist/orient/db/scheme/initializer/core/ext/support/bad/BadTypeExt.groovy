package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.bad

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * @author Vyacheslav Rusakov 
 * @since 02.07.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(BadTypeExtImpl)
@interface BadTypeExt {

}