package ru.vyarus.guice.persist.orient.repository.command.ext.elvar;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Map;

/**
 * Repository method el vars parameters descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ElVarDescriptor {

    /**
     * Possible variables values.
     * Should be defined for string or object placeholders to avoid injection.
     */
    public Multimap<String, String> values = HashMultimap.create();

    /**
     * Vars parameters indexes in repository method.
     */
    public Map<String, Integer> parametersIndex = Maps.newHashMap();

    /**
     * Parameters with type Class. They are converted to string by getting class name.
     */
    public Map<String, Integer> classParametersIndex = Maps.newHashMap();
}
