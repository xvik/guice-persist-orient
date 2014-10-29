package ru.vyarus.guice.persist.orient.finder.internal;

import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.internal.result.ResultDescriptor;

/**
 * Parsed method finder declaration.
 * Descriptor should be build one time and cached for future reuse.
 * Not immutable but after factory must be used in immutable way.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.AbstractClassWithoutAnyMethod"})
public abstract class FinderDescriptor {

    // when interface hierarchies used we can't get root class from method
    public Class finderRootType;

    public ResultDescriptor result;

    // assigned executor instance
    public FinderExecutor executor;
}
