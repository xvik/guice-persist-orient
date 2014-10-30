package ru.vyarus.guice.persist.orient.support.finder

import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.finder.Use
import ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate

/**
 * @author Vyacheslav Rusakov 
 * @since 31.10.2014
 */
@FinderDelegate(CustomMixinDelegate)
interface CustomMixin<TYPE, K> {

    List<TYPE> doSomething(int a, int b)

    List<TYPE> doSomething2(int a, int b, Class<?> c)

    List<TYPE> badCall()

    // explicitly define connection type. this affect implementation @FinderDb parameter
    @Use(DbType.GRAPH)
    List graphCall()

    void invocationFail()

    void paramSpecific(int a, Object b, K c)
}
