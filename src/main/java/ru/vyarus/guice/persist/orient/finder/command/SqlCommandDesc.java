package ru.vyarus.guice.persist.orient.finder.command;

import java.util.Map;

/**
 * Sql command description (created for each execution, can't be cached).
 *
 * @author Vyacheslav Rusakov
 * @since 31.07.2014
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class SqlCommandDesc {
    public boolean isFunctionCall;
    public String query;
    public boolean useNamedParams;
    public Object[] params;
    public Map<String, Object> namedParams;
    public int start;
    public int max;
}
