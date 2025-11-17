package ru.vyarus.guice.persist.orient.repository.mixin.support

import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * Check case when type specified as array and when type specified as list.
 *
 * @author Vyacheslav Rusakov 
 * @since 19.10.2014
 */
public interface ComplexGeneric2<I, T, K> {

    @Query('select from ${I}')
    T selectCustomArray()

    @Query('select from ${I}')
    K selectCustomList()
}