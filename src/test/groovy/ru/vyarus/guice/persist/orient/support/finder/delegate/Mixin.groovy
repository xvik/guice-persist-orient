package ru.vyarus.guice.persist.orient.support.finder.delegate

/**
 * @author Vyacheslav Rusakov 
 * @since 22.10.2014
 */
public interface Mixin<T> {
    void count();

    void add(int a, int b);

    void merge(T model, int a);
}