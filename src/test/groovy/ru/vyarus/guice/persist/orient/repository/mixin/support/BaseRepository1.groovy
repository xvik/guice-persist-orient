package ru.vyarus.guice.persist.orient.repository.mixin.support

import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * Sample of generic repository.
 * It can't be used directly but may be used by other repositories to provide common functionality.
 * Special placeholder ${T} will be resolved into generic type, provided in interface.
 *
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
public interface BaseRepository1<T> extends DeepBaseRepository<T> {

    @Query('select from ${T}')
    List<T> selectAll()
}