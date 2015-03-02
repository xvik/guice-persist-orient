package ru.vyarus.guice.persist.orient.repository.delegate.support.amend.ext

import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Dummy amend annotation to check that extension searched in proper context for delegates
 *
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@AmendMethod(DummyAmendExtension)
@interface DummyAmend {

    String value()

}