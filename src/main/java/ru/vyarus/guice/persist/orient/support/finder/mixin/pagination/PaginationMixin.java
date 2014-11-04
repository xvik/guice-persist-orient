package ru.vyarus.guice.persist.orient.support.finder.mixin.pagination;

import com.google.inject.persist.finder.Finder;
import com.google.inject.persist.finder.FirstResult;
import com.google.inject.persist.finder.MaxResults;

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
public interface PaginationMixin<M, R> extends Pagination<R> {

    /**
     * @param start starting entity (from 0)
     * @param max   number of items to select (to select all set value less or equal 0)
     * @return list of selected entities or empty list (if range not exist)
     */
    @Finder(query = "select from ${M}")
    List<R> getAll(@FirstResult int start, @MaxResults int max);

    /**
     * @return number of records
     */
    @Finder(query = "select count(@rid) from ${M}")
    int getCount();
}
