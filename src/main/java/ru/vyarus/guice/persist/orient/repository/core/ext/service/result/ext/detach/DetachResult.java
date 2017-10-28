package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach;

import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Result extension to detach result objects. Use detach to plain object.
 * <p>
 * Extension works in very simple way: if plain result, its being unproxied. In case of collection,
 * collection is cleared (to save the same collection type) and unproxied results added at the same order.
 * <p>
 * If used with objects created in this transaction (and so having temporal id in time of detach),
 * resulted pojo will be tracked and correct id set after transaction commit.
 * <p>
 * Be careful, because detach will lead to loading of entire object graph.
 * <p>
 * If used not with object connection, error will be thrown to indicate incorrect usage.
 *
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@ResultConverter(DetachResultExtension.class)
public @interface DetachResult {
}
