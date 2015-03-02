package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import com.google.inject.ImplementedBy;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;

/**
 * Converts repository method raw result into requested type.
 * Custom implementation may be registered in module {@code bind(ResultConverter.class).to(MyCustomImpl.class)}.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
@ImplementedBy(DefaultResultConverter.class)
public interface ResultConverter {

    /**
     * @param descriptor result descriptor (from main method descriptor)
     * @param result     raw result value
     * @return converted result
     * @throws ResultConversionException on conversion error
     */
    Object convert(ResultDescriptor descriptor, Object result);
}
