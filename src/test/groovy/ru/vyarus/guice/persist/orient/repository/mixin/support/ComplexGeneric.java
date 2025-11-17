package ru.vyarus.guice.persist.orient.repository.mixin.support

import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * @author Vyacheslav Rusakov 
 * @since 19.10.2014
 */
public interface ComplexGeneric<T, K extends Collection<T>> {

    @Query('select from ${T}')
    K selectAllComplex()
}