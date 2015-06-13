package ru.vyarus.guice.persist.orient.util.transactional

import com.orientechnologies.orient.core.tx.OTransaction
import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author Vyacheslav Rusakov 
 * @since 10.06.2015
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtensionAnnotation(TransactionalExtension)
@interface TransactionalTest {
    OTransaction.TXTYPE value() default OTransaction.TXTYPE.OPTIMISTIC;
}