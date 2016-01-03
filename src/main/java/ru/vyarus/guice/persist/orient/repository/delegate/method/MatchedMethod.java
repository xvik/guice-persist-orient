package ru.vyarus.guice.persist.orient.repository.delegate.method;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Target method temporal descriptor.
 * Used for best delegate method selection.
 *
 * @author Vyacheslav Rusakov
 * @since 08.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class MatchedMethod {
    /**
     * Method reference.
     */
    public Method method;

    /**
     * Parsed method parameters (excluding extensions).
     */
    public List<ParamInfo> paramInfos;

    /**
     * Extensions marker (true if parameters extensions used).
     */
    public boolean extended;

    public MatchedMethod(final Method method, final List<ParamInfo> paramInfos, final boolean extended) {
        this.method = method;
        this.paramInfos = paramInfos;
        this.extended = extended;
    }
}
