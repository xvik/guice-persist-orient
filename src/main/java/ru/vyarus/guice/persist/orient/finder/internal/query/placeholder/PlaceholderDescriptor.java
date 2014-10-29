package ru.vyarus.guice.persist.orient.finder.internal.query.placeholder;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.Collections;
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
        "PMD.DefaultPackage"})
public class PlaceholderDescriptor {

    public Multimap<String, String> values;
    public Map<String, Integer> parametersIndex;
    // parameters mapped from finder interface generic parameters definition
    public Map<String, String> genericParameters;

    /**
     * @return list of parameters indexes bound by placeholders
     */
    public List<Integer> getBoundIndexes() {
        // will be null if only generic parameters used
        return parametersIndex == null
                ? Collections.<Integer>emptyList()
                : Lists.newArrayList(parametersIndex.values());
    }
}
