package ru.vyarus.guice.persist.orient.finder.result;

/**
 * Result description for conversion
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
public class ResultDesc {
    public Object result;
    public ResultType type;
    // may be raw method return type or requested collection wrapper
    public Class returnClass;
    // collection generic or method return if single result
    public Class entityClass;

}
