package ru.vyarus.guice.persist.orient.repository.core.ext.support.exts

import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod

import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.METHOD
import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
@Target([METHOD, TYPE])
@Retention(RUNTIME)
@AmendMethod(CommandAmendExtension)
@interface CmdAmend {
}