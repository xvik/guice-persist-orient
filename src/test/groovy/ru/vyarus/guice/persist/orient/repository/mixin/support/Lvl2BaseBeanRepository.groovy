package ru.vyarus.guice.persist.orient.repository.mixin.support

import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * @author Vyacheslav Rusakov 
 * @since 18.10.2014
 */
class Lvl2BaseBeanRepository<T> {

    @Query('select from ${T}')
    public T selectOne() {
        throw new UnsupportedOperationException()
    }

    @Query('select from ${T}')
    public List<T> selectAll() {
        throw new UnsupportedOperationException()
    }

    @Query('select from ${T}')
    com.google.common.base.Optional<T> selectOptional() {
        throw new UnsupportedOperationException()
    }

    @Query('select from ${T}')
    Iterator<T> selectAllIterator() {
        throw new UnsupportedOperationException()
    }
}
