package ru.vyarus.guice.persist.orient.db;

/**
 * Database types enum.
 *
 * @author Vyacheslav Rusakov
 * @since 02.08.2014
 */
public enum DbType {
    /**
     * Document api connection.
     */
    DOCUMENT,
    /**
     * Object api connection.
     */
    OBJECT,
    /**
     * Graph api connection.
     */
    GRAPH,
    /**
     * Special value, used for default value, allowing specifying correct connection.
     */
    UNKNOWN
}
