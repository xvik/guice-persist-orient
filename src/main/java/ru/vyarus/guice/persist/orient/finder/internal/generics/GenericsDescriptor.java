package ru.vyarus.guice.persist.orient.finder.internal.generics;

import com.google.common.collect.Maps;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Definition of finder type super interfaces generic values.
 *
 * @author Vyacheslav Rusakov
 * @since 16.10.2014
 */
@SuppressWarnings({
        "checkstyle:visibilitymodifier",
        "PMD.DefaultPackage"})
public class GenericsDescriptor {
    public Class root;
    // super interface type -> generic name -> generic type (either class or parametrized type or generic array)
    public Map<Class, Map<String, Type>> types = Maps.newHashMap();
}
