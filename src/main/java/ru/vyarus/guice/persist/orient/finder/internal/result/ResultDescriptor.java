package ru.vyarus.guice.persist.orient.finder.internal.result;

import ru.vyarus.guice.persist.orient.finder.result.ResultType;

/**
 * Finder method result description.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"
})
public class ResultDescriptor {

    public ResultType returnType;
    // type to convert result object to or simply method return type
    public Class expectType;
    // return entity type (return type for single return and generic type for collection, array or iterator)
    public Class entityType;
}
