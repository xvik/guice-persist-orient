package ru.vyarus.guice.persist.orient.finder.internal.pagination;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Finder method pagination parameters descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"
})
public class PaginationDescriptor {
    // @FirstResult annotation
    public Integer firstResultParamIndex;
    // @MaxResults annotation
    public Integer maxResultsParamIndex;

    /**
     * @return list of param indexes used by pagination.
     */
    public List<Integer> getBoundIndexes() {
        final List<Integer> res = Lists.newArrayList(
                Objects.firstNonNull(firstResultParamIndex, -1),
                Objects.firstNonNull(maxResultsParamIndex, -1));
        res.remove(Integer.valueOf(-1));
        return res;
    }

    public boolean isEmpty() {
        return firstResultParamIndex == null && maxResultsParamIndex == null;
    }
}
