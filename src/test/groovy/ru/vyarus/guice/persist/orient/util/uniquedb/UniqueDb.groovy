package ru.vyarus.guice.persist.orient.util.uniquedb

import org.spockframework.runtime.extension.ExtensionAnnotation
import ru.vyarus.guice.persist.orient.util.remoteext.UseRemoteExtension

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Inits unique db name for test
 *
 * @author Vyacheslav Rusakov 
 * @since 10.10.2015
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(UniqueDbExtension)
@interface UniqueDb {
}
