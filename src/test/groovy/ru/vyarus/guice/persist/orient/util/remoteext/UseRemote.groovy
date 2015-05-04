package ru.vyarus.guice.persist.orient.util.remoteext

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Spock extension for re-using memory tests to check remote connection (with embedded server).
 * Usage: extend existing test with new class MyRemoteTest extends MyTest and annotate new class with @UseRemote.
 *
 * @author Vyacheslav Rusakov 
 * @since 03.05.2015
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(UseRemoteExtension)
@interface UseRemote {

}