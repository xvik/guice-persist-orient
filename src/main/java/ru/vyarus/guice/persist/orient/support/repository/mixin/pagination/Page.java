package ru.vyarus.guice.persist.orient.support.repository.mixin.pagination;

import java.util.List;

/**
 * Page object for paginated queries.
 *
 * @param <T> page item type
 * @author Vyacheslav Rusakov
 * @see PageSupport
 * @since 01.11.2014
 */
public class Page<T> {
    private final int currentPage;
    private final int totalPages;
    private final int totalCount;
    private final int pageSize;
    private final List<T> content;

    public Page(final int currentPage, final int totalPages, final int totalCount,
                final int pageSize, final List<T> content) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.content = content;
    }

    /**
     * @return current page number (starting from 1)
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @return total pages count
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @return total elements count (in db)
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @return page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return page items
     */
    public List<T> getContent() {
        return content;
    }
}
