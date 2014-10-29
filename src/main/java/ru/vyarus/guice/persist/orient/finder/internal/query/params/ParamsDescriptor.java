package ru.vyarus.guice.persist.orient.finder.internal.query.params;

import java.util.Map;

/**
 * Finder method parameters description.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"})
public class ParamsDescriptor {

    public boolean useNamedParameters;

    public Integer[] parametersIndex;
    public Map<String, Integer> namedParametersIndex;
}
