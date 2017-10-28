package ru.vyarus.guice.persist.orient.support.repository.mixin.pagination;

import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;

/**
 * Extended pagination: implementation use
 * {@link Pagination} to get total and
 * page content and aggregates it into single object.
 * <p>
 * Don't use directly, {@link Pagination}
 * already extends it. Additional interface introduced just to simplify navigation.
 *
 * @param <R> page item type
 * @author Vyacheslav Rusakov
 * @since 01.11.2014
 */
@Delegate(PageSupportDelegate.class)
public interface PageSupport<R> {

    /**
     * @param page     page number (starting from 1)
     * @param pageSize elements in page
     * @return entities page
     */
    Page<R> getPage(int page, int pageSize);
}
