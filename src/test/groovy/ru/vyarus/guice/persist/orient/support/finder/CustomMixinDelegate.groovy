package ru.vyarus.guice.persist.orient.support.finder

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderDb
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderGeneric
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderInstance

/**
 * @author Vyacheslav Rusakov 
 * @since 31.10.2014
 */
class CustomMixinDelegate implements CustomMixin {

    @Override
    List doSomething(int a, int b) {
        throw new UnsupportedOperationException()
    }

    List doSomething(int a, @FinderGeneric("TYPE") Class<?> type, int b, @FinderInstance CustomMixin finder) {
        return null
    }

    @Override
    List doSomething2(int a, int b, Class c) {
        throw new UnsupportedOperationException()
    }

    @Override
    List doSomething2(@FinderGeneric("TYPE") Class<?> type, int a, int b, Class c, @FinderDb Object connection) {
        return null
    }

    @Override
    List badCall() {
        throw new UnsupportedOperationException()
    }

    List badCall(@FinderDb OrientGraph connection) {
        // bad connection type - call to finder will fail
        return null;
    }

    @Override
    List graphCall() {
        throw new UnsupportedOperationException()
    }

    List graphCall(@FinderDb OrientGraph graph) {
        // connection type declared explicitly on interface using @Use annotation
        return null
    }

    @Override
    void invocationFail() {
        throw new IllegalStateException()
    }

    @Override
    void paramSpecific(int a, Object b, Object c) {
        throw new UnsupportedOperationException()
    }

    @Override
    void paramSpecific(int a, Object b, String c) {
        // most specific method must be chosen
    }
}
