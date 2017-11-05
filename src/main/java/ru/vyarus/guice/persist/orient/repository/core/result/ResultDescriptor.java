package ru.vyarus.guice.persist.orient.repository.core.result;

import ru.vyarus.guice.persist.orient.db.DbType;

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
     * Will never be primitive type even if method returns primitive (to simplify conversion logic as orient
     * never returns primitives)!
     */
    public Class expectType;
    /**
     * Return entity type: return type for single return and generic type for collection, array or iterator
     * (very important for proper executor detection).
     */
    public Class entityType;
    /**
     * Indicates result entity type (e.g. if pure document or object or vertex is required). Simplifies result
     * conversion logic (by avoiding redundant logic branches).
     * <p>
     * NOTE: this type is computed according to entity class and may be different from actual connection used (in
     * {@link ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor#executor}) because
     * executor could be selected by query connection hint or default executor used when executor could not be detected.
     * For example for return type int (e.g. counter query) document executor will be selected, but entity db type
     * will be unknown.
     */
    public DbType entityDbType;
}
