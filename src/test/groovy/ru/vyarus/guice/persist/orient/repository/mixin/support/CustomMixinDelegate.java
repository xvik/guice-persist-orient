package ru.vyarus.guice.persist.orient.repository.mixin.support;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.Connection;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.instance.Repository;

import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 31.10.2014
 */
public class CustomMixinDelegate implements CustomMixin {

    @Override
    public List doSomething(int a, int b) {
        throw new UnsupportedOperationException();
    }

    public List doSomething(int a, @Generic("TYPE") Class<?> type, int b, @Repository CustomMixin repository) {
        return null;
    }

    @Override
    public List doSomething2(int a, int b, Class c) {
        throw new UnsupportedOperationException();
    }

    public List doSomething2(@Generic("TYPE") Class<?> type, int a, int b, Class c, @Connection Object connection) {
        return null;
    }

    @Override
    public List badCall() {
        throw new UnsupportedOperationException();
    }

    public List badCall(@Connection OrientGraph connection) {
        // bad connection type - call to repository method will fail
        return null;
    }

    @Override
    public List graphCall() {
        throw new UnsupportedOperationException();
    }

    public List graphCall(@Connection OrientGraph graph) {
        // connection type declared explicitly on interface using @Use annotation
        return null;
    }

    @Override
    public void invocationFail() {
        throw new IllegalStateException();
    }

    @Override
    public void paramSpecific(int a, Object b, Object c) {
        throw new UnsupportedOperationException();
    }

    public void paramSpecific(int a, Object b, String c) {
        // most specific method must be chosen
    }

}
