package ru.vyarus.guice.persist.orient.repository.core.result;

/**
 * Type of returned result.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
public enum ResultType {
    /**
     * Plain object (single result, update query result or void).
     */
    PLAIN,
    /**
     * Any collection (Iterable) or Iterator.
     */
    COLLECTION,
    /**
     * Array.
     */
    ARRAY
}
