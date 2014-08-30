package ru.vyarus.guice.persist.orient.finder.internal;

import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.result.ResultType;

import java.util.Map;

/**
 * Parsed method finder declaration.
 * Descriptor should be build one time and cached for future reuse.
 * Not immutable but after factory must be used is immutable way.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"
})
public class FinderDescriptor {

    // Finder.namedQuery
    String functionName;
    // Finder.query
    String query;
    // @FirstResult annotation
    Integer firstResultParamIndex;
    // @MaxResults annotation
    Integer maxResultsParamIndex;

    boolean isFunctionCall;
    boolean useNamedParameters;
    ResultType returnType;
    // return entity type (return type for single return and generic type for collection, array or iterator)
    Class returnEntity;
    // type to convert result object to
    Class expectType;
    // assigned executor instance
    FinderExecutor executor;

    Integer[] parametersIndex;
    Map<String, Integer> namedParametersIndex;
}
