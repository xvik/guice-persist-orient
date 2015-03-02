package ru.vyarus.guice.persist.orient.repository.core.result;

/**
 * Type of returned result.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
public enum ResultType {
    /**
     * No result expected.
     */
    VOID,
    /**
     * Plain object (single result).
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
