package ru.vyarus.guice.persist.orient.finder.internal.placeholder;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;

/**
 * Finder method placeholder descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"
})
public class PlaceholderDescriptor {

    public Multimap<String, String> values;
    public Map<String, Integer> parametersIndex;

    /**
     * @return list of parameters indexes bound by placeholders
     */
    public List<Integer> getBoundIndexes() {
        return Lists.newArrayList(parametersIndex.values());
    }
}
