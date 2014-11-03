package ru.vyarus.guice.persist.orient.finder.result;

import ru.vyarus.guice.persist.orient.finder.internal.result.ResultDescriptor;

/**
 * Result description for conversion.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class ResultDesc {
    public Object result;
    public ResultType type;
    // may be raw method return type or requested collection wrapper
    public Class returnClass;
    // collection generic or method return if single result
    public Class entityClass;

    public ResultDesc() {
        // for tests
    }

    public ResultDesc(final ResultDescriptor descriptor, final Object result) {
        type = descriptor.returnType;
        returnClass = descriptor.expectType;
        entityClass = descriptor.entityType;
        this.result = result;
    }
}
