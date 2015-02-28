package ru.vyarus.guice.persist.orient.repository.core.util.support

import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 28.02.2015
 */
interface MethodToStringCases<T, K extends Model> {

    void noargs()

    void simple(Object obj, Class cls)

    void primitive(int t, long k)

    void generic(T t, K k)

    void array(Object[] obj)

    void vararg(Object... obj)
}