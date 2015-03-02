package ru.vyarus.guice.persist.orient.repository.core.spi.result;

import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;

import java.lang.annotation.Annotation;

/**
 * Result conversion extension, enabled by annotation annotated with
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter}.
 * <p>Result conversion is pretty universal and usually don't rely on method execution specifics, that's why
 * raw descriptor type used (but entire descriptor passed in order to use it as storage for
 * converter specific data).</p>
 * <p>Use {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConversionException}
 * for error cases.</p>
 *
 * @param <A> annotation type
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
public interface ResultExtension<A extends Annotation> {

    /**
     * Called on descriptor creation phase to initialize converted from possible annotation parameters.
     *
     * @param descriptor repository method descriptor
     * @param annotation extension annotation
     */
    void handleAnnotation(RepositoryMethodDescriptor descriptor, A annotation);

    /**
     * Called to convert method execution result. If default converted is enabled (in annotation
     * {@link ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter}), then will be called
     * after default converter
     * ({@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter}).
     *
     * @param descriptor repository method descriptor
     * @param result     result object
     * @return converted result
     */
    Object convert(RepositoryMethodDescriptor descriptor, Object result);
}
