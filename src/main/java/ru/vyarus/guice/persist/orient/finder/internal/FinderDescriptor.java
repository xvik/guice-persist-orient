package ru.vyarus.guice.persist.orient.finder.internal;

import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.internal.pagination.PaginationDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.params.ParamsDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.placeholder.PlaceholderDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.result.ResultDescriptor;

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
        "PMD.DefaultPackage"})
public class FinderDescriptor {
    // when interface hierarchies used we can't get root class from method
    Class finderRootType;

    boolean isFunctionCall;
    // finder query or function
    String query;

    PlaceholderDescriptor placeholders;
    ResultDescriptor result;

    // assigned executor instance
    FinderExecutor executor;

    ParamsDescriptor params;
    PaginationDescriptor pagination;
}
