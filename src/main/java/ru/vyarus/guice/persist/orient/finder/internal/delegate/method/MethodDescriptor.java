package ru.vyarus.guice.persist.orient.finder.internal.delegate.method;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Delegate method descriptor object.
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"})
public class MethodDescriptor {
    public Class target;
    public Method method;

    public List<Integer> extendedParamsPositions;
    public Map<Integer, Class> typeParams;
    public Integer instancePosition;
    public Integer connectionPosition;

    public MethodDescriptor(final Method method) {
        this.method = method;
    }

    public boolean isExtended() {
        return extendedParamsPositions != null;
    }
}
