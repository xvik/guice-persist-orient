package ru.vyarus.guice.persist.orient.repository.mixin.support

import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar

/**
 * @author Vyacheslav Rusakov 
 * @since 18.10.2014
 */
class BaseBeanRepository<K> extends Lvl2BaseBeanRepository<K> {

    @Query('select from ${K} where ${field} = ?')
    public List<K> findByField(@ElVar("field") String field, Object value) {
        throw new UnsupportedOperationException()
    }

    @Query('select from ${K}')
    public K[] selectCustom() {
        throw new UnsupportedOperationException()
    }
}
