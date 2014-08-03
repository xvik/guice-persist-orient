package ru.vyarus.guice.persist.orient.finder.command;

import java.util.Map;

/**
 * @author Vyacheslav Rusakov
 * @since 31.07.2014
 */
public class SqlCommandDesc {
    public boolean isFunctionCall;
    public String function;
    public String query;
    public boolean useNamedParams;
    public Object[] params;
    public Map<String, Object> namedParams;
    public int start;
    public int max;
}
