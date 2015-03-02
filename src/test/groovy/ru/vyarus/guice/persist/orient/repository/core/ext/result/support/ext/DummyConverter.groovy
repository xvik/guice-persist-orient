package ru.vyarus.guice.persist.orient.repository.core.ext.result.support.ext

import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.METHOD
import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@Target([METHOD, TYPE])
@Retention(RUNTIME)
@ResultConverter(DummyConverterExtension)
@interface DummyConverter {
    String value()
}