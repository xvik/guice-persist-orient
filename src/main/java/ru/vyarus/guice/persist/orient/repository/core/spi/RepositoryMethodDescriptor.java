package ru.vyarus.guice.persist.orient.repository.core.spi;

import com.google.common.collect.Maps;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.executor.RepositoryExecutor;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ResultConversionDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethodExtension;

import javax.inject.Provider;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Base class for parsed repository method descriptor.
 * Descriptor build one time then used for all future method calls.
 * Not immutable, but after initial creation must be used in immutable way.
 *
 * @param <E> extension type
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@SuppressWarnings({
        "checkstyle:VisibilityModifier",
        "PMD.AbstractClassWithoutAnyMethod"})
public abstract class RepositoryMethodDescriptor<E extends AmendExecutionExtension> {

    /**
     * May be set by method extension (during descriptor creation) to guide executor selection.
     */
    public DbType connectionHint;

    /**
     * May be set by method extension (during descriptor creation) to convert results to specific collection type.
     */
    public Class<? extends Collection> returnCollectionHint;

    /**
     * Extensions should store specific descriptors here.
     */
    public Map<String, Object> extDescriptors = Maps.newHashMap();

    /**
     * List of amend extensions, found on method.
     * Assigned by {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService}
     * after parameters processing.
     * <p>
     * Extensions must be processed manually in method extension implementation (because these extensions
     * are totally different for different method extensions).
     * <p>
     * Extensions in this list are sorted according to {@link ru.vyarus.guice.persist.orient.db.util.Order}
     * annotations.
     */
    public List<E> amendExtensions;

    // --------------------------------- set or computed automatically (assigned after method extension!)

    /**
     * Repository root type (required because not always method owner is root class).
     */
    public Class repositoryRootType;

    /**
     * Method extension handler instance, used internally to process method.
     * Set by {@link ru.vyarus.guice.persist.orient.repository.core.ext.SpiService}
     */
    public Provider<? extends RepositoryMethodExtension> methodExtension;

    /**
     * Analyzed result descriptor.
     */
    public ResultDescriptor result;

    /**
     * Assigned executor instance.
     */
    public RepositoryExecutor executor;

    /**
     * Result conversion analysed and used by
     * {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ResultService}.
     */
    public ResultConversionDescriptor resultConversion;
}
