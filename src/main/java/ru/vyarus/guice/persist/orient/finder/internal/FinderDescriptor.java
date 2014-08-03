package ru.vyarus.guice.persist.orient.finder.internal;

import ru.vyarus.guice.persist.orient.finder.FinderExecutor;

import java.util.Collection;
import java.util.Map;

/**
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
public class FinderDescriptor {
    static enum ReturnType {
        PLAIN, COLLECTION, ARRAY, ITERATOR
    }

    String functionName;
    String query;
    Integer firstResultParamIndex;
    Integer maxResultsParamIndex;
    Class<? extends Collection> returnCollectionType;

    boolean isFunctionCall;
    boolean useNamedParameters;
    ReturnType returnType;
    Class returnEntity;
    FinderExecutor executor;

    Integer[] parametersIndex;
    Map<String, Integer> namedParametersIndex;
}
