package ru.vyarus.guice.persist.orient.db.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to order object instances.
 * Use {@link OrderComparator} to order annotated instances.
 * <p>
 * Order is natural, so, for example, -1 appear before 100.
 * <p>
 * By default order is 0.
 *
 * @author Vyacheslav Rusakov
 * @since 07.02.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Order {

    /**
     * @return order value
     */
    int value() default 0;
}
