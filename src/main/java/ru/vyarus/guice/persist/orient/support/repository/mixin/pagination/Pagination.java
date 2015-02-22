package ru.vyarus.guice.persist.orient.support.repository.mixin.pagination;

import ru.vyarus.guice.persist.orient.repository.command.ext.pagination.Limit;
import ru.vyarus.guice.persist.orient.repository.command.ext.pagination.Skip;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;

import java.util.List;

/**
 * Mixin adds pagination methods for specified type.
 * This is the most simplest pagination, but it demonstrates all techniques for writing custom mixins,
 * so could be used as example for writing more specific mixins.
 *
 * @param <M> scheme model type
 * @param <R> return entity (Model for object connection and ODocument for document connection)
 * @author Vyacheslav Rusakov
 * @since 01.11.2014
 */
public interface Pagination<M, R> extends PageSupport<R> {

    /**
     * @param skip  skip results
     * @param limit number of items to select (to select all set value less or equal 0)
     * @return list of selected entities or empty list (if range not exist)
     */
    @Query("select from ${M}")
    List<R> getAll(@Skip int skip, @Limit int limit);

    /**
     * @return number of records
     */
    @Query("select count(@rid) from ${M}")
    int getCount();
}
