package ru.vyarus.guice.persist.orient.repository.mixin.support

import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar

/**
 * Sample of generic repository.
 * It can't be used directly but may be used by other repositories to provide common functionality.
 * Special placeholder ${T} will be resolved into generic type, provided in interface.
 *
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
public interface BaseRepository2<T, K> {

    @Query('select from ${T} where ${field} = ?')
    List<T> findByField(@ElVar("field") String field, Object value);
}