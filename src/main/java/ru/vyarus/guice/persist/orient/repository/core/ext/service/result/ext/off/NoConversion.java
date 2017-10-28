package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.off;

import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Result converter exception used to switch off default conversion mechanism (so raw value returned).
 * <p>
 * Note that default converter checks result compatibility with actual result object and performs
 * collection type conversion (if specific collection type requested in method annotation, e.g.
 * {@link ru.vyarus.guice.persist.orient.repository.command.query.Query} support it).
 *
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@ResultConverter(value = NoConversionResultExtension.class, applyDefaultConverter = false)
public @interface NoConversion {
}
