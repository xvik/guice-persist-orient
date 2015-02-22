package ru.vyarus.guice.persist.orient.repository.command.core.param;

import java.util.Map;

/**
 * Query parameters descriptor.
 *
 * @author Vyacheslav Rusakov
 * @see QueryParamsContext
 * @since 26.09.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"})
public class ParamsDescriptor {

    /**
     * Named variables marker.
     */
    public boolean useNamedParameters;

    /**
     * Positional parameters method arguments indexes.
     */
    public Integer[] parametersIndex;

    /**
     * Named parameters method arguments indexes.
     */
    public Map<String, Integer> namedParametersIndex;
}
