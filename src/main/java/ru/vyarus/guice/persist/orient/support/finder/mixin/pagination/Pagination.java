package ru.vyarus.guice.persist.orient.support.finder.mixin.pagination;

import ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate;

/**
 * Extended pagination: implementation use
 * {@link ru.vyarus.guice.persist.orient.support.finder.mixin.pagination.PaginationMixin} to get total and
 * page content and aggregates it into single object.
 * <p>Don't use directly, {@link ru.vyarus.guice.persist.orient.support.finder.mixin.pagination.PaginationMixin}
 * already extends it. Additional interface introduced just to simplify navigation.</p>
 *
 * @param <R> page item type
 * @author Vyacheslav Rusakov
 * @since 01.11.2014
 */
@FinderDelegate(PaginationDelegate.class)
public interface Pagination<R> {

    /**
     * @param page     page number (starting from 1)
     * @param pageSize elements in page
     * @return entities page
     */
    Page<R> getPage(int page, int pageSize);
}
