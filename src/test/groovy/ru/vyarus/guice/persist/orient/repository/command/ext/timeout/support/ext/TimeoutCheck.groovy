package ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support.ext

import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.ext.CheckCommandExtension
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * Text extension to validate that fetch parameter set to command by extension.
 *
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@AmendMethod(TimeoutCheckExtension)
@interface TimeoutCheck {

}