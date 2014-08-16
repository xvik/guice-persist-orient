package ru.vyarus.guice.persist.orient.finder.result;

import com.google.inject.ImplementedBy;

/**
 * Converts finder raw result into requested type.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
@ImplementedBy(DefaultResultConverter.class)
public interface ResultConverter {

    /**
     * @param desc conversion descriptor
     * @return converted result
     */
    Object convert(ResultDesc desc);
}
