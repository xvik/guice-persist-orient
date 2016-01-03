package ru.vyarus.guice.persist.orient.repository.core.result;

/**
 * Repository method result analysis descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ResultDescriptor {

    /**
     * Type of returned object.
     */
    public ResultType returnType;
    /**
     * Type to convert result object to or simply method return type
     * (affected by
     * {@link ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor#returnCollectionHint}).
     */
    public Class expectType;
    /**
     * Return entity type: return type for single return and generic type for collection, array or iterator
     * (very important for proper executor detection).
     */
    public Class entityType;
}
