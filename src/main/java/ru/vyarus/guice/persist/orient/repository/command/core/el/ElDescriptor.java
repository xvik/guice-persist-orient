package ru.vyarus.guice.persist.orient.repository.command.core.el;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Query variables descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 15.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ElDescriptor {

    /**
     * All variables found in query.
     */
    public List<String> vars;

    /**
     * Direct values, computed during descriptor creation.
     */
    public Map<String, String> directValues = Maps.newHashMap();

    /**
     * Dynamic variable, computed on each method execution (possibly, from method parameters).
     * These variables are handled by extensions, but extension must declare that variable is recognized.
     */
    public List<String> handledVars = Lists.newArrayList();

    public ElDescriptor(final List<String> vars) {
        this.vars = vars;
    }
}
