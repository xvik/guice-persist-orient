package ru.vyarus.guice.persist.orient.finder.internal.query;

import ru.vyarus.guice.persist.orient.finder.internal.FinderDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.query.pagination.PaginationDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.query.params.ParamsDescriptor;
import ru.vyarus.guice.persist.orient.finder.internal.query.placeholder.PlaceholderDescriptor;

/**
 * Finder query descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"})
public class FinderQueryDescriptor extends FinderDescriptor {
    public boolean isFunctionCall;
    // finder query or function
    public String query;

    public PlaceholderDescriptor placeholders;

    public ParamsDescriptor params;
    public PaginationDescriptor pagination;
}
