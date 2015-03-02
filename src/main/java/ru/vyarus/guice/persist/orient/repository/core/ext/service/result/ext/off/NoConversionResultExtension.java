package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.off;

import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultExtension;

import javax.inject.Singleton;

/**
 * {@link NoConversion} result extension.
 *
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
@Singleton
public class NoConversionResultExtension implements ResultExtension<NoConversion> {

    @Override
    public void handleAnnotation(final RepositoryMethodDescriptor descriptor, final NoConversion annotation) {
        // not needed
    }

    @Override
    public Object convert(final RepositoryMethodDescriptor descriptor, final Object result) {
        return result;
    }
}
