package ru.vyarus.guice.persist.orient.support.finder.mixin.pagination;

import com.google.common.base.Preconditions;
import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderInstance;

import java.util.List;

/**
 * Pagination implementation. Use calling finder instance to get required db data and compose resulting page.
 *
 * @author Vyacheslav Rusakov
 * @since 01.11.2014
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class PaginationDelegate implements Pagination {

    @SuppressWarnings("unchecked")
    public Page getPage(@FinderInstance final PaginationMixin finder, final int page, final int pageSize) {
        Preconditions.checkArgument(page > 0, "Page parameter must be > 0");
        Preconditions.checkArgument(pageSize > 0, "Page size must be > 0");
        final int count = finder.getCount();
        final int pagesTotal = (int) Math.ceil((double) count / pageSize);
        Preconditions.checkArgument(page <= pagesTotal, "Can't select page %s: total pages count %s",
                page, pagesTotal);
        final int start = (page - 1) * pageSize;
        final List content = finder.getAll(start, pageSize);
        return new Page(page, pagesTotal, count, pageSize, content);
    }
}
