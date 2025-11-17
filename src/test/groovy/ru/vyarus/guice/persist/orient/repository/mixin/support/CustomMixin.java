package ru.vyarus.guice.persist.orient.repository.mixin.support;

import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;

import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 31.10.2014
 */
@Delegate(CustomMixinDelegate.class)
public interface CustomMixin<TYPE, K> {

    List<TYPE> doSomething(int a, int b);

    List<TYPE> doSomething2(int a, int b, Class<?> c);

    List<TYPE> badCall();

    // explicitly define connection type. this affect implementation @Connection parameter
    // normally such delegate will simply use graph connection directly.
    @Delegate(value = CustomMixinDelegate.class, connection = DbType.GRAPH)
    List graphCall();

    void invocationFail();

    void paramSpecific(int a, Object b, K c);
}
