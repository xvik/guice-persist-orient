package ru.vyarus.guice.persist.orient.repository.core.ext.service.result;

import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultExtension;

import javax.inject.Provider;

/**
 * Result conversion descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ResultConversionDescriptor {

    /**
     * True when default
     * {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter}
     * must be applied.
     */
    public boolean useDefaultConverter;

    /**
     * Custom result conversion extension.
     */
    public Provider<? extends ResultExtension> customConverter;
}
