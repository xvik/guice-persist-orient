package ru.vyarus.guice.persist.orient.repository.command.core.spi;

import java.util.Map;

/**
 * Sql query descriptor object. Created for each execution and can't be cached.
 *
 * @author Vyacheslav Rusakov
 * @since 31.07.2014
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class SqlCommandDescriptor {

    /**
     * Query string (function or other query string).
     * Set by specific extension.
     */
    public String command;

    /**
     * Named parameters marker.
     */
    public boolean useNamedParams;

    /**
     * Ordinal parameters (empty if named parameters used).
     */
    public Object[] params;

    /**
     * Named parameters (empty if ordinal parameters used).
     */
    public Map<String, Object> namedParams;

    /**
     * Query string variables (used to substitute variables in string).
     * By default, populated with generic names, but extension could provide additional values.
     */
    public Map<String, String> elVars;
}
