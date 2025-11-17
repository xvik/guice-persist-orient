package ru.vyarus.guice.persist.orient.repository.mixin.support;

import com.google.common.base.Optional;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;

import java.util.Iterator;
import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 18.10.2014
 */
public class Lvl2BaseBeanRepository<T> {

    @Query("select from ${T}")
    public T selectOne() {
        throw new UnsupportedOperationException();
    }

    @Query("select from ${T}")
    public List<T> selectAll() {
        throw new UnsupportedOperationException();
    }

    @Query("select from ${T}")
    public Optional<T> selectOptional() {
        throw new UnsupportedOperationException();
    }

    @Query("select from ${T}")
    public Iterator<T> selectAllIterator() {
        throw new UnsupportedOperationException();
    }

}
