package ru.vyarus.guice.persist.orient.db.util;

import java.util.Comparator;

/**
 * Comparator to order objects, annotated with {@link Order}.
 *
 * @author Vyacheslav Rusakov
 * @since 07.02.2015
 */
public class OrderComparator implements Comparator<Object> {

    public static final OrderComparator INSTANCE = new OrderComparator();

    @Override
    public int compare(final Object o1, final Object o2) {
        return getOrder(o1).compareTo(getOrder(o2));
    }

    private Integer getOrder(final Object obj) {
        final Order order = obj != null ? obj.getClass().getAnnotation(Order.class) : null;
        return order != null ? order.value() : 0;
    }
}
