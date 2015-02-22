package ru.vyarus.guice.persist.orient.repository.command.ext.placeholder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Map;

/**
 * Repository method placeholders descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"})
public class PlaceholderDescriptor {

    /**
     * Possible placeholder values (defined with
     * {@link ru.vyarus.guice.persist.orient.repository.command.ext.placeholder.PlaceholderValues}).
     * Possible values should be defined for string placeholders.
     */
    public Multimap<String, String> values = HashMultimap.create();

    /**
     * Placeholder parameters indexes in repository method.
     */
    public Map<String, Integer> parametersIndex = Maps.newHashMap();

}
