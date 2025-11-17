package ru.vyarus.guice.persist.orient.repository.mixin.support;

import com.google.common.base.Optional;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;

import java.util.Iterator;

/**
 * Repository on 2nd hierarchy level (PowerRepository -> BaseRepository1 -> DeepBaseRepository).
 *
 * @author Vyacheslav Rusakov
 * @since 16.10.2014
 */
public interface DeepBaseRepository<K> {

    @Query("select from ${K}")
    K selectOne();

    @Query("select from ${K}")
    K[] selectCustom();

    @Query("select from ${K}")
    Optional<K> selectOptional();

    @Query("select from ${K}")
    Iterator<K> selectAllIterator();
}
